package dev.client.tenacity.module;

import dev.client.tenacity.module.Category;
import dev.client.tenacity.module.Module;
import dev.client.tenacity.module.impl.combat.AutoHead;
import dev.client.tenacity.module.impl.combat.AutoPot;
import dev.client.tenacity.module.impl.combat.Criticals;
import dev.client.tenacity.module.impl.combat.Fastbow;
import dev.client.tenacity.module.impl.combat.KillAura;
import dev.client.tenacity.module.impl.combat.TargetStrafe;
import dev.client.tenacity.module.impl.combat.Velocity;
import dev.client.tenacity.module.impl.exploit.AntiAura;
import dev.client.tenacity.module.impl.exploit.AntiInvis;
import dev.client.tenacity.module.impl.exploit.Disabler;
import dev.client.tenacity.module.impl.exploit.Regen;
import dev.client.tenacity.module.impl.exploit.ResetVL;
import dev.client.tenacity.module.impl.misc.AntiDesync;
import dev.client.tenacity.module.impl.misc.AntiFreeze;
import dev.client.tenacity.module.impl.misc.AntiTabComplete;
import dev.client.tenacity.module.impl.misc.AutoHypixel;
import dev.client.tenacity.module.impl.misc.AutoRespawn;
import dev.client.tenacity.module.impl.misc.LightningTracker;
import dev.client.tenacity.module.impl.misc.NoRotate;
import dev.client.tenacity.module.impl.misc.Spammer;
import dev.client.tenacity.module.impl.movement.FastLadder;
import dev.client.tenacity.module.impl.movement.Flight;
import dev.client.tenacity.module.impl.movement.InventoryMove;
import dev.client.tenacity.module.impl.movement.LongJump;
import dev.client.tenacity.module.impl.movement.Scaffold;
import dev.client.tenacity.module.impl.movement.Speed;
import dev.client.tenacity.module.impl.movement.Sprint;
import dev.client.tenacity.module.impl.movement.Step;
import dev.client.tenacity.module.impl.player.AntiVoid;
import dev.client.tenacity.module.impl.player.AutoArmor;
import dev.client.tenacity.module.impl.player.Blink;
import dev.client.tenacity.module.impl.player.ChestStealer;
import dev.client.tenacity.module.impl.player.FastPlace;
import dev.client.tenacity.module.impl.player.Freecam;
import dev.client.tenacity.module.impl.player.InvManager;
import dev.client.tenacity.module.impl.player.NoFall;
import dev.client.tenacity.module.impl.player.NoSlow;
import dev.client.tenacity.module.impl.player.SafeWalk;
import dev.client.tenacity.module.impl.player.SpeedMine;
import dev.client.tenacity.module.impl.render.Ambience;
import dev.client.tenacity.module.impl.render.Animations;
import dev.client.tenacity.module.impl.render.ArraylistMod;
import dev.client.tenacity.module.impl.render.BlurModule;
import dev.client.tenacity.module.impl.render.Brightness;
import dev.client.tenacity.module.impl.render.ChinaHat;
import dev.client.tenacity.module.impl.render.ClickGuiMod;
import dev.client.tenacity.module.impl.render.ESP2D;
import dev.client.tenacity.module.impl.render.GlowESP;
import dev.client.tenacity.module.impl.render.HudMod;
import dev.client.tenacity.module.impl.render.NotificationsMod;
import dev.client.tenacity.module.impl.render.Radar;
import dev.client.tenacity.module.impl.render.ScoreboardMod;
import dev.client.tenacity.module.impl.render.SessionStats;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleCollection {
    public static boolean reloadModules;
    private HashMap<Class<? extends Module>, Module> modules = new HashMap();
    private final List<Class<? extends Module>> hiddenModules = new ArrayList<>(Arrays.asList(ArraylistMod.class, NotificationsMod.class));

    public List<Class<? extends Module>> getHiddenModules() {
        return hiddenModules;
    }

    public List<Module> getModules() {
        return new ArrayList<Module>(this.modules.values());
    }

    public void setModules(HashMap<Class<? extends Module>, Module> modules) {
        this.modules = modules;
    }

    public void init() {
        this.modules.put(AutoHead.class, new AutoHead());
        this.modules.put(AutoPot.class, new AutoPot());
        this.modules.put(Criticals.class, new Criticals());
        this.modules.put(Fastbow.class, new Fastbow());
        this.modules.put(KillAura.class, new KillAura());
        this.modules.put(TargetStrafe.class, new TargetStrafe());
        this.modules.put(Velocity.class, new Velocity());
        this.modules.put(AntiAura.class, new AntiAura());
        this.modules.put(AntiInvis.class, new AntiInvis());
        this.modules.put(Disabler.class, new Disabler());
        this.modules.put(Regen.class, new Regen());
        this.modules.put(ResetVL.class, new ResetVL());
        this.modules.put(AntiDesync.class, new AntiDesync());
        this.modules.put(AntiFreeze.class, new AntiFreeze());
        this.modules.put(AntiTabComplete.class, new AntiTabComplete());
        this.modules.put(AutoHypixel.class, new AutoHypixel());
        this.modules.put(AutoRespawn.class, new AutoRespawn());
        this.modules.put(LightningTracker.class, new LightningTracker());
        this.modules.put(NoRotate.class, new NoRotate());
        this.modules.put(Spammer.class, new Spammer());
        this.modules.put(FastLadder.class, new FastLadder());
        this.modules.put(Flight.class, new Flight());
        this.modules.put(InventoryMove.class, new InventoryMove());
        this.modules.put(LongJump.class, new LongJump());
        this.modules.put(Scaffold.class, new Scaffold());
        this.modules.put(Speed.class, new Speed());
        this.modules.put(Sprint.class, new Sprint());
        this.modules.put(Step.class, new Step());
        this.modules.put(AntiVoid.class, new AntiVoid());
        this.modules.put(AutoArmor.class, new AutoArmor());
        this.modules.put(Blink.class, new Blink());
        this.modules.put(ChestStealer.class, new ChestStealer());
        this.modules.put(FastPlace.class, new FastPlace());
        this.modules.put(Freecam.class, new Freecam());
        this.modules.put(InvManager.class, new InvManager());
        this.modules.put(NoFall.class, new NoFall());
        this.modules.put(NoSlow.class, new NoSlow());
        this.modules.put(SafeWalk.class, new SafeWalk());
        this.modules.put(SpeedMine.class, new SpeedMine());
        this.modules.put(Ambience.class, new Ambience());
        this.modules.put(Animations.class, new Animations());
        this.modules.put(ArraylistMod.class, new ArraylistMod());
        this.modules.put(BlurModule.class, new BlurModule());
        this.modules.put(Brightness.class, new Brightness());
        this.modules.put(ChinaHat.class, new ChinaHat());
        this.modules.put(ClickGuiMod.class, new ClickGuiMod());
        this.modules.put(ESP2D.class, new ESP2D());
        this.modules.put(GlowESP.class, new GlowESP());
        this.modules.put(HudMod.class, new HudMod());
        this.modules.put(NotificationsMod.class, new NotificationsMod());
        this.modules.put(Radar.class, new Radar());
        this.modules.put(ScoreboardMod.class, new ScoreboardMod());
        this.modules.put(SessionStats.class, new SessionStats());
    }

    public List<Module> getModulesInCategory(Category c) {
        return this.modules.values().stream().filter(m -> m.getCategory() == c).collect(Collectors.toList());
    }

    public Module get(Class<? extends Module> mod) {
        return this.modules.get(mod);
    }

    public Module getModuleByName(String name) {
        return this.modules.values().stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModulesContains(String text) {
        return this.modules.values().stream().filter(m -> m.getName().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
    }

    public final List<Module> getToggledModules() {
        return this.modules.values().stream().filter(Module::isToggled).collect(Collectors.toList());
    }
}