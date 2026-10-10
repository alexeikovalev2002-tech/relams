package com.mogayt;

import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.*;

public class Config {

    private static Path file;

    public static void init() {
        file = FabricLoader.getInstance().getConfigDir().resolve("mog-mod.txt");
        load();
    }

    public static void save() {
        if (file == null) return;
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(file))) {
            w.println("espEnabled=" + BlockEspMod.espEnabled);
            w.println("oreType=" + BlockEspMod.oreType);
            w.println("oreDistance=" + BlockEspMod.oreDistance);
            w.println("chestEspEnabled=" + BlockEspMod.chestEspEnabled);
            w.println("chestType=" + BlockEspMod.chestType);
            w.println("chestDistance=" + BlockEspMod.chestDistance);
            w.println("playerEspEnabled=" + BlockEspMod.playerEspEnabled);
            w.println("playerDistance=" + BlockEspMod.playerDistance);
            w.println("itemEspEnabled=" + BlockEspMod.itemEspEnabled);
            w.println("itemDistance=" + BlockEspMod.itemDistance);
            w.println("aimMobs=" + AimMobs.enabled);
            w.println("aimSpeed=" + AimMobs.aimSpeed);
            w.println("aimDistance=" + AimMobs.aimDistance);
            w.println("aimFov=" + AimMobs.fovAngle);
            w.println("aimBot=" + AimBot.enabled);
            w.println("aimBotDelayMin=" + AimBot.delayMin);
            w.println("aimBotDelayMax=" + AimBot.delayMax);
            w.println("aimBotDist=" + AimBot.distance);
            w.println("fullbright=" + Fullbright.enabled);
            w.println("freecam=" + Freecam.enabled);
            w.println("optimizer=" + Optimizer.enabled);
            w.println("trajectory=" + TrajectoryPredictor.enabled);
            w.println("k.esp=" + KeyBinds.espKey);
            w.println("k.chest=" + KeyBinds.chestKey);
            w.println("k.player=" + KeyBinds.playerKey);
            w.println("k.item=" + KeyBinds.itemKey);
            w.println("k.aimMobs=" + KeyBinds.aimMobsKey);
            w.println("k.aimBot=" + KeyBinds.aimBotKey);
            w.println("k.fullbright=" + KeyBinds.fullbrightKey);
            w.println("k.freecam=" + KeyBinds.freecamKey);
            w.println("k.optimizer=" + KeyBinds.optimizerKey);
            w.println("k.trajectory=" + KeyBinds.trajectoryKey);
            for (int i = 0; i < ChatBind.binds.size(); i++) {
                ChatBind.Bind b = ChatBind.binds.get(i);
                w.println("bind." + i + ".cmd=" + b.command);
                w.println("bind." + i + ".key=" + b.keyCode);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void load() {
        if (file == null || !Files.exists(file)) return;
        java.util.Map<Integer, String> cmds = new java.util.HashMap<>();
        java.util.Map<Integer, Integer> keys = new java.util.HashMap<>();

        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                int eq = line.indexOf('=');
                if (eq < 0) continue;
                String k = line.substring(0, eq);
                String v = line.substring(eq + 1);
                try {
                    if (k.equals("espEnabled")) BlockEspMod.espEnabled = Boolean.parseBoolean(v);
                    else if (k.equals("oreType")) BlockEspMod.oreType = Integer.parseInt(v);
                    else if (k.equals("oreDistance")) BlockEspMod.oreDistance = Integer.parseInt(v);
                    else if (k.equals("chestEspEnabled")) BlockEspMod.chestEspEnabled = Boolean.parseBoolean(v);
                    else if (k.equals("chestType")) BlockEspMod.chestType = Integer.parseInt(v);
                    else if (k.equals("chestDistance")) BlockEspMod.chestDistance = Integer.parseInt(v);
                    else if (k.equals("playerEspEnabled")) BlockEspMod.playerEspEnabled = Boolean.parseBoolean(v);
                    else if (k.equals("playerDistance")) BlockEspMod.playerDistance = Integer.parseInt(v);
                    else if (k.equals("itemEspEnabled")) BlockEspMod.itemEspEnabled = Boolean.parseBoolean(v);
                    else if (k.equals("itemDistance")) BlockEspMod.itemDistance = Integer.parseInt(v);
                    else if (k.equals("aimMobs")) AimMobs.enabled = Boolean.parseBoolean(v);
                    else if (k.equals("aimSpeed")) AimMobs.aimSpeed = Double.parseDouble(v);
                    else if (k.equals("aimDistance")) AimMobs.aimDistance = Double.parseDouble(v);
                    else if (k.equals("aimFov")) AimMobs.fovAngle = Double.parseDouble(v);
                    else if (k.equals("aimBot")) AimBot.enabled = Boolean.parseBoolean(v);
                    else if (k.equals("aimBotDelayMin")) AimBot.delayMin = Double.parseDouble(v);
                    else if (k.equals("aimBotDelayMax")) AimBot.delayMax = Double.parseDouble(v);
                    else if (k.equals("aimBotDist")) AimBot.distance = Double.parseDouble(v);
                    else if (k.equals("fullbright")) Fullbright.enabled = Boolean.parseBoolean(v);
                    else if (k.equals("freecam")) Freecam.enabled = Boolean.parseBoolean(v);
                    else if (k.equals("optimizer")) Optimizer.enabled = Boolean.parseBoolean(v);
                    else if (k.equals("trajectory")) TrajectoryPredictor.enabled = Boolean.parseBoolean(v);
                    else if (k.equals("k.esp")) KeyBinds.espKey = Integer.parseInt(v);
                    else if (k.equals("k.chest")) KeyBinds.chestKey = Integer.parseInt(v);
                    else if (k.equals("k.player")) KeyBinds.playerKey = Integer.parseInt(v);
                    else if (k.equals("k.item")) KeyBinds.itemKey = Integer.parseInt(v);
                    else if (k.equals("k.aimMobs")) KeyBinds.aimMobsKey = Integer.parseInt(v);
                    else if (k.equals("k.aimBot")) KeyBinds.aimBotKey = Integer.parseInt(v);
                    else if (k.equals("k.fullbright")) KeyBinds.fullbrightKey = Integer.parseInt(v);
                    else if (k.equals("k.freecam")) KeyBinds.freecamKey = Integer.parseInt(v);
                    else if (k.equals("k.optimizer")) KeyBinds.optimizerKey = Integer.parseInt(v);
                    else if (k.equals("k.trajectory")) KeyBinds.trajectoryKey = Integer.parseInt(v);
                    else if (k.startsWith("bind.")) {
                        String[] p = k.split("\\.");
                        if (p.length == 3) {
                            int idx = Integer.parseInt(p[1]);
                            if (p[2].equals("cmd")) cmds.put(idx, v);
                            else if (p[2].equals("key")) keys.put(idx, Integer.parseInt(v));
                        }
                    }
                } catch (Exception ignored) {}
            }
        } catch (IOException e) { e.printStackTrace(); }

        if (!cmds.isEmpty()) {
            java.util.List<ChatBind.Bind> loaded = new java.util.ArrayList<>();
            for (Integer idx : cmds.keySet()) {
                int key = keys.getOrDefault(idx, -1);
                loaded.add(new ChatBind.Bind(cmds.get(idx), key));
            }
            ChatBind.binds.clear();
            ChatBind.binds.addAll(loaded);
        }
    }
                                }
