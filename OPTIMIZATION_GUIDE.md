# Eaglercraft 1.8 Performance Optimization Guide

## Overview

This document outlines performance optimization strategies for Eaglercraft 1.8, inspired by modern Minecraft mods: **Sodium**, **Lithium**, **OptiFine**, **Starlight**, **FerriteCore**, and **EntityCulling**.

**Important:** This guide provides architectural strategies and optimization targets. Implementation requires careful testing and understanding of the specific code patterns used in this TeaVM-based WebAssembly project.

---

## Key Optimization Areas

### 1. **Memory & Allocation (FerriteCore-inspired)**

**Target:** Reduce garbage collection pressure and allocation frequency

**Strategies:**
- Reuse buffer objects instead of allocating new ones each frame
- Use object pooling for frequently-created temporary objects
- Cache results of expensive computations (entity lookups, collision checks)
- Reduce ArrayList allocations in hot loops

**Files to examine:**
- [src/main/java/net/lax1dude/eaglercraft/v1_8/opengl/](src/main/java/net/lax1dude/eaglercraft/v1_8/opengl/) - Buffer allocation patterns
- [src/main/java/net/lax1dude/eaglercraft/v1_8/internal/buffer/](src/main/java/net/lax1dude/eaglercraft/v1_8/internal/buffer/) - Custom buffer types

---

### 2. **Rendering Pipeline (Sodium-inspired)**

**Target:** Reduce GPU overhead and draw calls

**Strategies:**
- Batch multiple chunk updates together to amortize compilation cost
- Spread chunk rebuilds across multiple frames instead of one frame
- Optimize frustum culling to reduce vertex data submission
- Cache chunk visibility results

**Files to optimize:**
- [RenderGlobal.java](src/game/java/net/minecraft/client/renderer/RenderGlobal.java) - Main rendering orchestrator
- [RenderChunk.java](src/game/java/net/minecraft/client/renderer/chunk/RenderChunk.java) - Per-chunk rendering
- [ViewFrustum.java](src/game/java/net/minecraft/client/renderer/ViewFrustum.java) - Frustum culling
- [ChunkCompileTaskGenerator.java](src/game/java/net/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator.java) - Chunk compilation scheduling

**Implementation Pattern:**
```java
// Instead of immediate rebuild on block change:
markBlockForUpdate(x, y, z);  // Queue for later processing

// Process batches periodically:
processPendingUpdates();  // Spread across frames
```

---

### 3. **Entity Systems (Lithium-inspired)**

**Target:** Reduce per-entity overhead in AI, movement, and collision

**Strategies:**
- Cache spatial lookups (reduce entity search iterations)
- Implement entity culling (don't process entities outside view frustum)
- Optimize entity AI tick scheduling (not every entity every tick)
- Cache collision detection results within frame

**Files to optimize:**
- [Entity.java](src/game/java/net/minecraft/entity/Entity.java) - Entity base class (`moveEntity()`, collision methods)
- [World.java](src/game/java/net/minecraft/world/World.java) - Entity queries
- [EntityAIAttackOnCollide.java](src/game/java/net/minecraft/entity/ai/EntityAIAttackOnCollide.java) - AI pathfinding
- [PathFinder.java](src/game/java/net/minecraft/pathfinding/PathFinder.java) - A* search optimization

**Implementation Pattern:**
```java
// Cache entity lookups instead of linear search:
for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(excludeEntity, aabb)) {
    // Process entity
}
// Cache this result and invalidate only when needed
```

---

### 4. **Game Logic (Lithium-inspired)**

**Target:** Reduce CPU overhead in block ticking, redstone, and world updates

**Strategies:**
- Batch block update notifications
- Cache redstone wire connection states
- Optimize block tick scheduling (not every block every tick)
- Reduce redundant neighbor checks

**Files to optimize:**
- [WorldServer.java](src/game/java/net/minecraft/world/WorldServer.java) - Block ticking (`tickUpdates()`)
- [BlockRedstoneWire.java](src/game/java/net/minecraft/block/BlockRedstoneWire.java) - Redstone propagation
- [World.java](src/game/java/net/minecraft/world/World.java) - Block update queuing

**Implementation Pattern:**
```java
// Queue block updates for batching:
world.scheduleBlockUpdate(x, y, z, block, delay);  // Don't update neighbors immediately

// Process updates in batches:
processPendingBlockUpdates();  // Spread work across multiple ticks
```

---

### 5. **Lighting (Starlight-inspired)**

**Target:** Optimize light propagation and recalculation (future work)

**Strategies:**
- Cache light level results
- Implement faster light propagation algorithm
- Reduce light update frequency
- Optimize brightness calculation

**Files to analyze:**
- [WorldRenderer.java](src/main/java/net/lax1dude/eaglercraft/v1_8/opengl/WorldRenderer.java) - Lighting buffers
- [EaglerDeferredPipeline.java](src/main/java/net/lax1dude/eaglercraft/v1_8/opengl/EaglerDeferredPipeline.java) - Deferred lighting

---

## Optimization Implementation Workflow

### Phase 1: Profiling
1. Enable performance metrics in main game loop
2. Identify slowest operations (rendering, entity updates, block ticking)
3. Establish baseline metrics (FPS, frame time, memory usage)

### Phase 2: Memory Optimization
1. Implement buffer/object pooling for hot allocations
2. Test with heavy rendering loads (many chunks, particles)
3. Measure GC pause reduction

### Phase 3: Rendering Optimization
1. Implement chunk update batching
2. Add frame-spreading for chunk rebuilds
3. Test with rapid block placement/breaking

### Phase 4: Entity Optimization
1. Add entity culling (don't process outside view)
2. Implement entity lookup caching
3. Test with many mobs spawned

### Phase 5: Logic Optimization
1. Batch block updates
2. Cache redstone calculations
3. Test with complex redstone circuits

### Phase 6: Validation
1. Compare before/after metrics
2. Verify game behavior unchanged
3. Test edge cases (rapid updates, boundary conditions)

---

## Code Patterns to Look For

### Pattern 1: Allocation in Hot Loop
```java
// BAD - allocates every iteration:
for (int i = 0; i < 1000; i++) {
    List<Entity> entities = new ArrayList<>();
    // ...
}

// GOOD - reuse allocation:
List<Entity> entities = new ArrayList<>();
for (int i = 0; i < 1000; i++) {
    entities.clear();
    // ...
}
```

### Pattern 2: Linear Search
```java
// BAD - O(n) search every frame:
for (Entity entity : allEntities) {
    if (entity.getPosition().distance(player) < 10) {
        process(entity);
    }
}

// GOOD - cached spatial lookup:
List<Entity> nearby = spatialCache.getEntitiesNear(player, 10);
for (Entity entity : nearby) {
    process(entity);
}
```

### Pattern 3: Immediate Update
```java
// BAD - immediate update:
onBlockUpdate(x, y, z) {
    updateChunk();  // Expensive
}

// GOOD - deferred batching:
onBlockUpdate(x, y, z) {
    markDirty(x, y, z);  // Queue
}

// Later, process in batch:
processDirtyChunks();  // Amortized cost
```

---

## Performance Targets

| Metric | Baseline | Target | Strategy |
|--------|----------|--------|----------|
| Chunk rebuild time | ~50ms | ~30ms | Batching + amortization |
| Entity render calls | 1000+ | 600+ | Culling + frustum |
| Memory allocations/frame | 500+ | 200+ | Pooling + caching |
| Collision time | ~2ms | ~1ms | Caching + early termination |
| Redstone update time | ~10ms | ~5ms | Wire state caching |
| Frame time variance | ±20ms | ±5ms | Spreading work across frames |

---

## Testing & Verification

### Benchmark Scenarios

**Scenario 1: High Entity Load**
- Spawn 500+ mobs
- Measure FPS stability and entity tick time

**Scenario 2: Rapid Block Updates**
- Place/break blocks in 100x100 area
- Measure frame drops and chunk rebuild time

**Scenario 3: Complex Redstone**
- Build extensive redstone circuit
- Measure TPS and update propagation time

**Scenario 4: Large Render Distance**
- Set render distance to 16+
- Measure memory usage and chunk loading time

### Profiling Tools

Use browser DevTools (F12) to profile:
- JavaScript execution time (expensive paths)
- Memory snapshots (allocation patterns)
- Performance timeline (frame budgets)

---

## Integration Checklist

- [ ] Profile current performance and establish baseline
- [ ] Identify top bottlenecks from profiling data
- [ ] Implement memory pooling for top bottleneck allocation
- [ ] Test and measure GC impact
- [ ] Implement chunk batching scheduler
- [ ] Test frame rate stability with block updates
- [ ] Add entity culling to renderer
- [ ] Test with many entities spawned
- [ ] Implement collision detection caching
- [ ] Test entity movement behavior
- [ ] Batch block update notifications
- [ ] Test redstone circuits
- [ ] Validate all game mechanics unchanged
- [ ] Document performance improvements

---

## Key Insights for WebAssembly

This project targets **WebAssembly + TeaVM**, which has different optimization characteristics than native Java:

1. **GC is more critical** - Browser GC can stall the entire tab
2. **Memory is limited** - Browsers typically limit WASM modules to 2-4GB
3. **WASM->JS boundary** is expensive - Minimize cross-boundary calls
4. **Browser optimizations matter** - V8 JIT can make a huge difference
5. **Allocation patterns affect JIT** - Consistent types perform better

Focus first on **memory pooling** and **GC reduction** as these have the biggest impact in browser environments.

---

## References

- Sodium (rendering): https://github.com/CaffeineMC/sodium-fabric
- Lithium (game logic): https://github.com/CaffeineMC/lithium-fabric  
- OptiFine (comprehensive): https://optifine.net/
- Starlight (lighting): https://github.com/PaperMC/Starlight
- FerriteCore (memory): https://github.com/malte0811/FerriteCore
- EntityCulling: https://github.com/tr7zw/EntityCulling
