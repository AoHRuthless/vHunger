/**
 * HungerUtils.java is a part of the plugin vHunger.
 *
 * Copyright (C) 2013 Anand Kumar <http://dev.bukkit.org/bukkit-plugins/vhunger/>
 *
 * vHunger is a free software: You can redistribute it or modify it
 * under the terms of the GNU General Public License published by the Free
 * Software Foundation, either version 3 of the license of any later version.
 * 
 * vHunger is distributed in the intent of being useful. However, there
 * is NO WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You can view a copy of the GNU General Public License at 
 * <http://www.gnu.org/licenses/> if you have not received a copy.
 */
package com.valygard.vhunger.util;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.valygard.vhunger.Hunger;

/**
 * @author Anand
 *
 */
public class HungerUtils {
	
	private static Hunger plugin;
	
	public HungerUtils(Hunger plugin) {
		HungerUtils.plugin	= plugin;
	}
	
	/*
	 * We need a method to check if the world is valid for our custom hunger
	 * properties.
	 */
	public static boolean isValidWorld(Player p)  {
		String world = p.getWorld().getName();
		List<String> worldList = plugin.getConfig().getStringList("worlds");
		
		if (worldList.contains(world))
			return true;
		
		return false;
	}
	
	public static void addEffect(Player p, PotionEffectType potion, int hunger) {
		if (!isValidWorld(p) || isExempt(p) || !isEnabled())
			return;
		
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("effects." + potion.toString().toLowerCase());
		
		// We can't add a potion effect that isn't configured.
		if (section == null)
			return;
		
		// If the hunger isn't the right amount, return.
		if (hunger != section.getInt("hunger-at-activation"))
			return;
		
		int amplifier = section.getInt("amplifier");
		// Multiply by 20 because potion effect duration is in ticks.
		int duration = section.getInt("seconds") * 20;
		
		// If either of the numbers are "null" (0), set them to a default.
		if (duration == 0)
			duration = 5;
		
		if (amplifier == 0)
			amplifier = 1;
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(potion.toString().toUpperCase()), duration, amplifier));
		p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 2.5F, 2.5F);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', section.getString("message")));
	}
	
	public static void clearEffect(Player p) {
		p.getActivePotionEffects().clear();
	}
	
	// Check if the effects part of this plugin is enabled.
	public static boolean isEnabled() {
		if (!plugin.getConfig().getBoolean("global-settings.enabled"))
			return false;
		return true;
	}
	
	public static boolean isExempt(Player p) {
		if (p.hasPermission("hunger.effects.exempt"))
			return true;
		return false;
	}
	
	
}
