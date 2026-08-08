package net.minecraft.client.renderer;

public class SodiumOptimizer {

    public static final SodiumOptimizer instance = new SodiumOptimizer();

    // Toggleable settings
    public boolean sodiumChunkRebuilding = true;
    public boolean entityFrustumCulling = true;
    public boolean fastMath = true;
    public int rebuildBudget = 16; // Chunks per frame budget

    // Performance Stats for Debug / Menu display
    public int culledEntitiesThisFrame = 0;
    public int renderedEntitiesThisFrame = 0;
    public int pendingChunkRebuilds = 0;

    public void resetFrameCounters() {
        this.culledEntitiesThisFrame = 0;
        this.renderedEntitiesThisFrame = 0;
    }

    public String getRebuildBudgetName() {
        switch (rebuildBudget) {
            case 8: return "Low (8)";
            case 16: return "Medium (16)";
            case 32: return "High (32)";
            case 64: return "Ultra (64)";
            default: return rebuildBudget + " Chunks";
        }
    }

    public void cycleRebuildBudget() {
        if (rebuildBudget == 8) rebuildBudget = 16;
        else if (rebuildBudget == 16) rebuildBudget = 32;
        else if (rebuildBudget == 32) rebuildBudget = 64;
        else rebuildBudget = 8;
    }
}
