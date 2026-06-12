# Optimization Implementation Guide for EaglercraftX 1.8

## Overview

This document provides detailed guidance on implementing performance optimizations for Eaglercraft 1.8 running in WebAssembly via TeaVM. The optimizations are inspired by Minecraft mods like Sodium, Lithium, and EntityCulling.

**Note:** Implementation requires careful study of EaglercraftX-specific code patterns, particularly the custom buffer APIs and TeaVM compilation quirks.

---

## Phase 1: Understanding the Codebase

### Key Files to Study

**Buffer Management:**
- `src/main/java/net/lax1dude/eaglercraft/v1_8/EagRuntime.java` - Custom buffer allocation API
- `src/main/java/net/lax1dude/eaglercraft/v1_8/internal/buffer/` - Custom buffer implementations

**Rendering Pipeline:**
- `src/game/java/net/minecraft/client/renderer/RenderGlobal.java` - Main render loop
- `src/game/java/net/minecraft/client/renderer/chunk/RenderChunk.java` - Per-chunk rendering
- `src/main/java/net/lax1dude/eaglercraft/v1_8/opengl/WorldRenderer.java` - GPU buffer management

**Entity Systems:**
- `src/game/java/net/minecraft/entity/Entity.java` - Base entity class
- `src/game/java/net/minecraft/world/World.java` - Entity queries and management
- `src/game/java/net/minecraft/entity/ai/` - AI implementations

**World Ticking:**
- `src/game/java/net/minecraft/world/WorldServer.java` - Server-side world
- `src/game/java/net/minecraft/world/World.java` - Block update system

---

## Phase 2: Memory Optimization (GC Pressure Reduction)

### Priority: HIGH | Effort: MEDIUM | Impact: CRITICAL in Browser

In WebAssembly, garbage collection is more critical than native Java because GC pauses freeze the entire browser tab.

### Strategy: Buffer Pooling

**Goal:** Reuse buffers instead of allocating new ones every frame

**Study Existing Pattern:**

Look at how `EagRuntime` allocates buffers:
```
EagRuntime.allocateByteBuffer(capacity)    // Custom buffer type
EagRuntime.allocateFloatBuffer(capacity)   // Custom buffer type  
EagRuntime.allocateIntBuffer(capacity)     // Custom buffer type
```

**Implementation Approach:**

1. Create a `BufferPool` class in `src/main/java/net/lax1dude/eaglercraft/v1_8/opengl/`
2. Use **custom EaglercraftX buffer types**, not Java NIO buffers
3. Implement thread-safe queue for pooling (ConcurrentLinkedQueue)
4. Set reasonable pool size limits (e.g., 256 buffers per type)

**Critical:** Import from `net.lax1dude.eaglercraft.v1_8.internal.buffer.*`, not `java.nio.*`

**Integration Points:**
- `WorldRenderer.java` - GPU buffer allocation
- Particle rendering systems
- Mesh generation in chunk compilation
- Display list buffers

### Strategy: Object Pooling for Temporary Collections

**Goal:** Reduce ArrayList/HashMap allocations in hot loops

**Target Methods:**
- Spatial queries (entity lookups)
- Collision detection loops
- Rendering frustum checks

**Implementation Approach:**

1. Create reusable collection instances at class level
2. Clear and reuse instead of allocating new
3. Be careful with TeaVM - some patterns may not optimize well

**Example Pattern:**
```java
// In class scope:
private List<Entity> entityBuffer = new ArrayList<>();

// In hot loop:
entityBuffer.clear();
world.getEntitiesWithinAABB(aabb, entityBuffer);  // Pass collection
for (Entity e : entityBuffer) {
    // Process
}
```

---

## Phase 3: Rendering Optimization (Frame Batching)

### Priority: MEDIUM | Effort: MEDIUM | Impact: HIGH

### Strategy: Chunk Update Batching

**Goal:** Spread chunk rebuilds across multiple frames instead of doing them all at once

**Study Existing Pattern:**

Look at:
- `RenderGlobal.markBlocksForUpdate()` - Where block changes trigger updates
- `ChunkCompileTaskGenerator.java` - How chunk compilation is scheduled
- `ViewFrustum.java` - Chunk visibility management

**Implementation Approach:**

1. Create update batching in `RenderGlobal`:
```java
private Queue<ChunkCoordinates> dirtyChunks = new LinkedList<>();
private int updateBudget = 256;  // Max chunk updates per frame

public void markBlocksForUpdate(...) {
    // Instead of immediate rebuild, queue for later
    dirtyChunks.addAll(getAffectedChunks(...));
}

public void updateChunks() {
    // In render loop: process batched updates
    int processed = 0;
    while (!dirtyChunks.isEmpty() && processed < updateBudget) {
        ChunkCoordinates coords = dirtyChunks.poll();
        markChunkForRebuild(coords);
        processed++;
    }
}
```

2. Call `updateChunks()` during render loop
3. Test with rapid block placement/breaking
4. Measure frame time variance - should be smoother

---

## Phase 4: Entity Optimization (Culling & Caching)

### Priority: HIGH | Effort: MEDIUM | Impact: HIGH

### Strategy: Entity Visibility Culling

**Goal:** Don't process/render entities outside the view frustum

**Study Existing Pattern:**

Look at:
- `RenderGlobal.renderEntities()` - Entity rendering loop
- Frustum culling in chunk rendering
- Entity position tracking

**Implementation Approach:**

1. Add frustum checks to entity rendering:
```java
// In RenderGlobal.renderEntities():
for (Entity entity : visibleEntities) {
    // Check if entity AABB is in frustum
    if (!frustum.contains(entity.boundingBox)) {
        continue;  // Skip rendering
    }
    renderEntity(entity);
}
```

2. For more complex culling, implement spatial partitioning:
   - Divide world into grid cells
   - Track which cells contain entities
   - Only check entities in nearby cells

3. Test with many mobs spawned
4. Measure draw call reduction

### Strategy: Entity Lookup Caching

**Goal:** Cache spatial queries to avoid O(n) searches

**Study Existing Pattern:**

Look at:
- `World.getEntitiesWithinAABBExcludingEntity()`
- How entity AI performs lookups
- Current entity storage structure

**Implementation Approach:**

1. Keep track of when entities move
2. Only update spatial cache when entities move significantly
3. Use a simple grid-based spatial index:

```java
private Map<Long, List<Entity>> spatialGrid = new HashMap<>();
private static final int CELL_SIZE = 16;  // 16 blocks

private long getGridKey(double x, double z) {
    return (((long)(x / CELL_SIZE) << 32) | 
            ((long)(z / CELL_SIZE) & 0xFFFFFFFFL));
}

// On entity movement:
updateEntityInGrid(entity);

// For lookups:
List<Entity> getEntitiesNear(double x, double z, double range) {
    List<Entity> result = new ArrayList<>();
    int minGridX = (int)(x / CELL_SIZE) - 1;
    int maxGridX = (int)(x / CELL_SIZE) + 1;
    int minGridZ = (int)(z / CELL_SIZE) - 1;
    int maxGridZ = (int)(z / CELL_SIZE) + 1;
    
    for (int gx = minGridX; gx <= maxGridX; gx++) {
        for (int gz = minGridZ; gz <= maxGridZ; gz++) {
            List<Entity> cellEntities = spatialGrid.get(getGridKey(gx * CELL_SIZE, gz * CELL_SIZE));
            if (cellEntities != null) {
                result.addAll(cellEntities);
            }
        }
    }
    return result;
}
```

---

## Phase 5: Game Logic Optimization (Block Updates)

### Priority: MEDIUM | Effort: LOW | Impact: MEDIUM

### Strategy: Block Update Batching

**Goal:** Process block updates in batches instead of individually

**Study Existing Pattern:**

Look at:
- `WorldServer.tickUpdates()` - Block update scheduling
- `World.updateBlockTick()` - Per-block updates
- Redstone wire propagation

**Implementation Approach:**

1. Collect block updates during the tick
2. Process them in organized batches:

```java
private Queue<BlockUpdateEvent> pendingUpdates = new LinkedList<>();

public void onBlockChange(int x, int y, int z) {
    pendingUpdates.add(new BlockUpdateEvent(x, y, z));
}

public void processPendingUpdates() {
    // Group updates by chunk
    Map<Long, List<BlockUpdateEvent>> byChunk = new HashMap<>();
    while (!pendingUpdates.isEmpty()) {
        BlockUpdateEvent evt = pendingUpdates.poll();
        long chunkKey = ChunkCoordinates.getKey(evt.x >> 4, evt.z >> 4);
        byChunk.computeIfAbsent(chunkKey, k -> new ArrayList<>()).add(evt);
    }
    
    // Process chunk by chunk
    for (List<BlockUpdateEvent> chunkUpdates : byChunk.values()) {
        processChunkUpdates(chunkUpdates);
    }
}
```

2. This reduces neighbor notification overhead
3. Test with complex redstone circuits

---

## Phase 6: Profiling & Measurement

### Tools Available

1. **Browser DevTools (F12):**
   - Performance tab: frame timeline analysis
   - Memory tab: heap snapshots and allocation tracking
   - Console: Custom performance logging

2. **Add Performance Metrics:**
```java
// Simple profiling in critical sections:
long startTime = System.nanoTime();
// ... expensive operation ...
long elapsed = (System.nanoTime() - startTime) / 1_000_000;
System.out.println("Operation took: " + elapsed + "ms");
```

3. **Key Metrics to Track:**
   - Frame time (target: <16ms for 60 FPS)
   - Memory usage
   - GC pause duration
   - Entity update time
   - Chunk rebuild time
   - Draw call count

### Benchmark Scenarios

**Test 1: Heavy Rendering**
- Load world with high render distance (16+)
- Monitor frame time and memory
- Expected improvement: -20 to -40% frame time

**Test 2: Entity Performance**
- Spawn 500+ mobs
- Monitor entity tick time
- Expected improvement: -30 to -50% with culling

**Test 3: Rapid Block Updates**
- Build/break blocks rapidly in small area
- Monitor frame stability
- Expected improvement: Smoother, fewer frame drops

**Test 4: Redstone Circuits**
- Complex redstone with many updates
- Monitor TPS impact
- Expected improvement: -20 to -30% TPS cost

---

## Implementation Checklist

- [ ] Study EaglercraftX buffer API and patterns
- [ ] Baseline measurements (FPS, memory, GC pauses)
- [ ] Implement buffer pooling
- [ ] Test pooling with rendering load
- [ ] Implement chunk update batching
- [ ] Test with block updates
- [ ] Implement entity frustum culling
- [ ] Test with many entities
- [ ] Implement spatial entity grid
- [ ] Test entity lookup performance
- [ ] Implement block update batching
- [ ] Test with redstone circuits
- [ ] Compare before/after metrics
- [ ] Verify game behavior unchanged
- [ ] Document final improvements

---

## Common Pitfalls

1. **TeaVM Compilation Issues**
   - Some Java patterns don't compile well to WebAssembly
   - Avoid complex generics, heavy reflection
   - Test builds frequently

2. **Memory Limits**
   - WASM modules have memory limits (typically 2-4GB in browsers)
   - Don't create unbounded caches
   - Set reasonable pool sizes

3. **Synchronization**
   - Browser is single-threaded
   - Avoid synchronized blocks if possible
   - Use ConcurrentLinkedQueue for simple cases

4. **JIT Optimization**
   - V8 performs best with consistent types
   - Avoid polymorphism in hot loops
   - Prefer primitive arrays over ArrayList

---

## Next Steps

1. Start with **Phase 1** (understand the codebase)
2. Implement **Phase 2** (buffer pooling) - highest impact in browser
3. Test thoroughly before moving to Phase 3
4. Measure and validate improvements
5. Document all changes for future maintainers

---

## References

- TeaVM: https://teavm.org/
- Sodium (rendering): https://github.com/CaffeineMC/sodium-fabric
- Lithium (game logic): https://github.com/CaffeineMC/lithium-fabric
- Browser Performance: https://developer.chrome.com/docs/devtools/performance/
