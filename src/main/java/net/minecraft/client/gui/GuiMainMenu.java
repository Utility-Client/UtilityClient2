package net.minecraft.client.gui;import com.google.gson.reflect.TypeToken;import de.gamingcraft.UtilityClient;import de.gamingcraft.utils.json.JSONUtils;import de.gamingcraft.utils.json.objects.CapeOwner;import de.gamingcraft.utils.json.objects.Release;import net.minecraft.client.Minecraft;import net.minecraft.client.main.Main;import net.minecraft.client.renderer.GlStateManager;import net.minecraft.client.renderer.OpenGlHelper;import net.minecraft.client.resources.I18n;import net.minecraft.util.EnumChatFormatting;import net.minecraft.util.MathHelper;import net.minecraft.util.ResourceLocation;import net.minecraft.world.demo.DemoWorldServer;import net.minecraft.world.storage.ISaveFormat;import net.minecraft.world.storage.WorldInfo;import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;import org.lwjgl.opengl.GLContext;import java.io.IOException;import java.net.MalformedURLException;import java.net.URI;import java.net.URL;import java.util.List;import java.util.Random;public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback{    private static final Logger logger = LogManager.getLogger();    private static final Random RANDOM = new Random();    private final float updateCounter;    private final Object threadLock = new Object();    private String openGLWarning1;    private String openGLWarning2;    private String openGLWarningLink;    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";    private int field_92024_r;    private int field_92022_t;    private int field_92021_u;    private int field_92020_v;    private int field_92019_w;    private Release release;    public GuiMainMenu() {        this.openGLWarning2 = field_96138_a;        this.updateCounter = RANDOM.nextFloat();        this.openGLWarning1 = "";        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported())        {            this.openGLWarning1 = I18n.format("title.oldgl1");            this.openGLWarning2 = I18n.format("title.oldgl2");            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";        }        try {            String rawJsonRelease = JSONUtils.downloadJson(new URL("https://api.github.com/repos/Utility-Client/UtilityClient2/releases/latest"));            release = (Release) JSONUtils.parseToJson(rawJsonRelease, new TypeToken<Release>(){}.getType());        } catch (MalformedURLException e) {            e.printStackTrace();        }    }    public boolean doesGuiPauseGame()    {        return false;    }    protected void keyTyped(char typedChar, int keyCode) throws IOException { }    public void initGui()    {        int j = this.height / 4 + 48;        int offset = -(this.height / 4);        this.buttonList.add(new GuiButton(1, this.width / 2 - 100 + offset, j, I18n.format("menu.singleplayer")));        this.buttonList.add(new GuiButton(2, this.width / 2 - 100 + offset, j + 24, I18n.format("menu.multiplayer")));        this.buttonList.add(new GuiButton(0, this.width / 2 - 100 + offset, j + 24 * 2, I18n.format("menu.options")));        this.buttonList.add(new GuiButton(4, this.width / 2 - 100 + offset, j + 24 * 3, I18n.format("menu.quit")));        synchronized (this.threadLock)        {            int field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);            this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);            int k = Math.max(field_92023_s, this.field_92024_r);            this.field_92022_t = (this.width - k) / 2;            this.field_92021_u = this.buttonList.get(0).yPosition - 24;            this.field_92020_v = this.field_92022_t + k;            this.field_92019_w = this.field_92021_u + 24;        }        this.mc.func_181537_a(false);    }    protected void actionPerformed(GuiButton button) throws IOException    {        if (button.id == 0)        {            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));        }        if (button.id == 5)        {            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));        }        if (button.id == 1)        {            this.mc.displayGuiScreen(new GuiSelectWorld(this));        }        if (button.id == 2)        {            this.mc.displayGuiScreen(new GuiMultiplayer(this));        }        if (button.id == 4)        {            this.mc.shutdown();        }        if (button.id == 11)        {            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);        }        if (button.id == 12)        {            ISaveFormat isaveformat = this.mc.getSaveLoader();            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");            if (worldinfo != null)            {                GuiYesNo guiyesno = GuiSelectWorld.func_152129_a(this, worldinfo.getWorldName(), 12);                this.mc.displayGuiScreen(guiyesno);            }        }    }    public void confirmClicked(boolean result, int id)    {        if (result && id == 12)        {            ISaveFormat isaveformat = this.mc.getSaveLoader();            isaveformat.flushCache();            isaveformat.deleteWorldDirectory("Demo_World");            this.mc.displayGuiScreen(this);        }        else if (id == 13)        {            if (result)            {                try                {                    Class<?> oclass = Class.forName("java.awt.Desktop");                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);                    oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new URI(this.openGLWarningLink));                }                catch (Throwable throwable)                {                    logger.error("Couldn't open link", throwable);                }            }            this.mc.displayGuiScreen(this);        }    }    int _longest = 0;    public void drawScreen(int mouseX, int mouseY, float partialTicks)    {        this.drawUtilityClientBackground();        GlStateManager.enableAlpha();        int i = 274;        int j = this.width / 2 - i / 2 -(this.height / 4);        int k = 30;        this.mc.getTextureManager().bindTexture(minecraftTitleTextures);        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);        int sg = 10;        this.drawTexturedModalRect(j, k, 0, 0, 155 + sg, 44);        this.drawTexturedModalRect(j + 155, k, 0, 45, 155, 44);        GlStateManager.pushMatrix();        GlStateManager.translate((float)(this.width / 2 + 90), 70.0F, 0.0F);        GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);        float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);        GlStateManager.scale(f, f, f);        GlStateManager.popMatrix();        String[] changes = release.body.split("-");        try {            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());            if(_longest != 0) {                drawRect(_longest - 4, 0, sr.getScaledWidth(), sr.getScaledHeight(), 1428160512);            }            int center = (changes.length * 10) / 2;            int y = (sr.getScaledWidth() - _longest) / 2 - this.fontRendererObj.getStringWidth(release.name + " - " + release.tag_name) / 2;            if(_longest != 0) {                this.fontRendererObj.drawStringWithShadow(release.name + " - " + release.tag_name, _longest + y, sr.getScaledHeight() / 2 - center - 10, 16777215);            }            int index = 0;            int longest = 0;            for (String str : changes) {                str = str.replace("\r",""). replace("\n","");                if(_longest != 0) {                    this.fontRendererObj.drawStringWithShadow(str, _longest - 2, sr.getScaledHeight() / 2 - center + (index * 10), 16777215);                }                int length = this.fontRendererObj.getStringWidth(str);                if(length > longest) {                    longest = length;                }                index++;            }            _longest = sr.getScaledWidth() - longest - 4;        }catch (Exception ignored) {        }        //String s = UtilityClient.getClientName() + " " + UtilityClient.getVersion();        //this.drawString(this.fontRendererObj, s, 2, this.height - 10, -1);        //String s1 = "Made by GamingCraft_hd";        //this.drawString(this.fontRendererObj, s1, 2, this.height - 20, -1);        if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0)        {            drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);            this.drawString(this.fontRendererObj, this.openGLWarning1, this.field_92022_t, this.field_92021_u, -1);            this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.field_92024_r) / 2, this.buttonList.get(0).yPosition - 12, -1);        }        super.drawScreen(mouseX, mouseY, partialTicks);    }    /**     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton     */    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException    {        super.mouseClicked(mouseX, mouseY, mouseButton);        synchronized (this.threadLock)        {            if (this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w)            {                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);                guiconfirmopenlink.disableSecurityWarning();                this.mc.displayGuiScreen(guiconfirmopenlink);            }        }    }}