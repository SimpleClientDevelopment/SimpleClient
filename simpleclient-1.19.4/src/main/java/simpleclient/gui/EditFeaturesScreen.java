package simpleclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import simpleclient.adapter.ItemRendererAdapter;
import simpleclient.adapter.ItemRendererAdapterImpl;
import simpleclient.adapter.TextRendererAdapter;
import simpleclient.adapter.TextRendererAdapterImpl;
import simpleclient.feature.*;
import simpleclient.text.Text;
import simpleclient.util.DrawUtil;

import java.util.List;

public class EditFeaturesScreen extends Screen {
    public DraggableFeature active;
    public double offsetX = 0;
    public double offsetY = 0;
    public int scroll = 0;

    public EditFeaturesScreen() {
        super(Component.translatable("simpleclient.edit_features"));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        TextRendererAdapter textRenderer = new TextRendererAdapterImpl(poseStack, font);
        ItemRendererAdapter itemRenderer = new ItemRendererAdapterImpl(poseStack);
        // Render Renderable Features
        for (Feature feature : FeatureManager.INSTANCE.getFeatures()) {
            if (feature instanceof RenderableFeature rf && rf.isEnabled()) {
                rf.renderDummy(textRenderer, itemRenderer, width, height);
                if (rf instanceof DraggableFeature df) {
                    int x1 = df.getXPos(width) - 1;
                    int y1 = df.getYPos(height) - 1;
                    int x2 = df.getXPos(width) + df.getWidth(textRenderer, itemRenderer, width, height) + 1;
                    int y2 = df.getYPos(height) + df.getHeight(textRenderer, itemRenderer, width, height) + 1;
                    GuiComponent.fill(poseStack, x1 - 1, y1 - 1, x2 + 1, y1, 0xFFAAAAAA);
                    GuiComponent.fill(poseStack, x1 - 1, y2, x2 + 1, y2 + 1, 0xFFAAAAAA);
                    GuiComponent.fill(poseStack, x1 - 1, y1 - 1, x1, y2 + 1, 0xFFAAAAAA);
                    GuiComponent.fill(poseStack, x2, y1 - 1, x2 + 1, y2 + 1, 0xFFAAAAAA);
                }
            }
        }
        // Render Enableable Features
        GuiComponent.fill(poseStack, 0, 0, width / 4, height, 0x77000000);
        List<Feature> features = FeatureManager.INSTANCE.getFeatures();
        for (int i = 0; i < features.size(); i++) {
            Feature feature = features.get(i);
            int count = (int) Math.ceil((double) width / 4 / 100);
            int x = i % count;
            int y = i / count;
            int wSize = (width / 4 - 2 - count * 2) / count;
            int wX1 = 2 + (2 + wSize) * x;
            int wY1 = 2 + 2 + (2 + wSize) * y;
            int wX2 = (2 + wSize) * x + wSize;
            int wY2 = 2 + (2 + wSize) * y + wSize;
            // Background
            int corners = (wY2 - wY1) / 8;
            DrawUtil.roundedRectangle(poseStack, wX1, wY1 + this.scroll, wX2, wY2 + this.scroll, corners, 0xFF555555);
            // Enable Button
            if (feature instanceof EnableableFeature ef) {
                int height = wSize / 6;
                DrawUtil.stadium(poseStack, wX1 + wSize / 10, scroll + wY2 - wSize / 10 - wSize / 3 / 2,
                        wX1 + wSize / 10 + wSize / 3, scroll + wY2 - wSize / 10,
                        ef.isEnabled() ? 0xFF00FF00 : 0xFFFF0000);
                if (ef.isEnabled()) {
                    DrawUtil.circle(poseStack, wX1 + wSize / 10 + wSize / 3 - height / 2,
                            scroll + wY2 - wSize / 10 - height / 2, height * 2 / 5, 0xFF000000);
                } else {
                    DrawUtil.circle(poseStack, wX1 + wSize / 10 + height / 2, scroll + wY2 - wSize / 10 - height / 2,
                            height * 2 / 5, 0xFF000000);
                }
            }
            // Config Button
            if (feature.hasConfig()) {
                poseStack.pushPose();
                int h = wSize / 3 / 2;
                int cogwheelX = wX1 + wSize / 10 * 4 + wSize / 3;
                int cogwheelY = scroll + wY2 - wSize / 10 - wSize / 3 / 2;
                if (cogwheelX <= mouseX && mouseX <= cogwheelX + h &&
                    cogwheelY <= mouseY && mouseY <= cogwheelY + h) {
                    float degrees = 360.0F / 300 * ((float) (System.currentTimeMillis() % 3000) / 10);
                    poseStack.rotateAround(Axis.ZP.rotationDegrees(degrees), cogwheelX + h / 2, cogwheelY + h / 2, 0.0F);
                }
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, new ResourceLocation("simpleclient", "textures/settings.png"));
                GuiComponent.blit(poseStack, cogwheelX, cogwheelY, 0, 0, h, h, h, h);
                poseStack.popPose();
            }
            // Name
            poseStack.pushPose();
            float scaleX = (((float) wSize) * 0.8F) / textRenderer.getWidth(feature.getName().split(" - ")[0]);
            float scaleY = (((float) wSize) * 0.3F) / textRenderer.getHeight();
            float scale = Math.min(scaleX, scaleY);
            poseStack.scale(scale, scale, scale);
            int yOffset = 0;
            for (String str : feature.getName().split(" - ")) {
                textRenderer.render(Text.literal(str), (int) ((wX1 + (wSize - textRenderer.getWidth(str) * scale) / 2) / scale), (int) ((scroll + wY1 + wSize * 0.1F + yOffset) / scale), 0xffffff);
                yOffset += textRenderer.getHeight();
            }
            poseStack.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        TextRendererAdapter textRenderer = new TextRendererAdapterImpl(null, font);
        ItemRendererAdapter itemRenderer = new ItemRendererAdapterImpl(null);
        active = getFeature(textRenderer, itemRenderer, mouseX, mouseY);
        if (active != null) {
            offsetX = mouseX - active.getXPos(width);
            offsetY = mouseY - active.getYPos(height);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (active != null) {
            active.setXPos((int) (mouseX - offsetX), width);
            active.setYPos((int) (mouseY - offsetY), height);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (active == null) {
            List<Feature> features = FeatureManager.INSTANCE.getFeatures();
            for (int i = 0; i < features.size(); i++) {
                Feature feature = features.get(i);
                int count = (int) Math.ceil((double) width / 4 / 100);
                int x = i % count;
                int y = i / count;
                int wSize = (width / 4 - 2 - count * 2) / count;
                int wX1 = 2 + (2 + wSize) * x;
                int wY1 = 2 + 2 + (2 + wSize) * y;
                int wX2 = (2 + wSize) * x + wSize;
                int wY2 = 2 + (2 + wSize) * y + wSize;
                int h = wSize / 3 / 2;
                int cogwheelX = wX1 + wSize / 10 * 4 + wSize / 3;
                int cogwheelY = scroll + wY2 - wSize / 10 - wSize / 3 / 2;
                if (feature.hasConfig() &&
                    cogwheelX <= mouseX && mouseX <= cogwheelX + h &&
                    cogwheelY <= mouseY && mouseY <= cogwheelY + h) {
                    minecraft.setScreen(new EditFeatureConfigScreen(feature, this));
                } else if (feature instanceof EnableableFeature ef &&
                             wX1 <= mouseX && mouseX <= wX2 &&
                    wY1 + scroll <= mouseY && mouseY <= wY2 + scroll) {
                    ef.setEnabled(!ef.isEnabled());
                }
            }
        } else active = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private DraggableFeature getFeature(TextRendererAdapter textRenderer, ItemRendererAdapter itemRenderer, double mouseX, double mouseY) {
        for (Feature feature : FeatureManager.INSTANCE.getFeatures()) {
            if (feature instanceof DraggableFeature df && df.isEnabled()) {
                int x1 = df.getXPos(width) - 1;
                int y1 = df.getYPos(height) - 1;
                int x2 = df.getXPos(width) + df.getWidth(textRenderer, itemRenderer, width, height) + 1;
                int y2 = df.getYPos(height) + df.getHeight(textRenderer, itemRenderer, width, height) + 1;
                if (x1 <= mouseX && mouseX <= x2 &&
                    y1 <= mouseY && mouseY <= y2) return df;
            }
        }
        return null;
    }

    @Override
    public void removed() {
        FeatureManager.INSTANCE.getJson().save();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll += amount * height / 30;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}