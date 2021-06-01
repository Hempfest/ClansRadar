package com.github.sanctum.clansradar.listeners;

import com.github.sanctum.clans.construct.Claim;
import com.github.sanctum.clans.construct.DefaultClan;
import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.clans.construct.extra.Resident;
import com.github.sanctum.clans.util.events.clans.ClaimResidentEvent;
import com.github.sanctum.labyrinth.data.Region;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TimeWatch;
import com.github.sanctum.map.AsyncMapFormatEvent;
import com.github.sanctum.map.structure.MapPoint;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class Controller implements Listener {


	@EventHandler
	public void test(ClaimResidentEvent e) {
		Claim c = e.getClaim();
		Clan owner = c.getClan();
		Resident r = e.getResident();


		TimeWatch.Recording recording = TimeWatch.Recording.from(r.timeActiveInMillis());

		Message.form(e.getResident().getPlayer()).action(owner.getColor() + owner.getName() + ": &r" + recording.getDays() + "d " + recording.getHours() + "hr " + recording.getMinutes() + "m " + recording.getSeconds() + "s");
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void test(AsyncMapFormatEvent e) {

		List<String> top_new = new LinkedList<>();
		top_new.add(" ");
		e.setAddedLinesTop(top_new);

		List<String> clans = new LinkedList<>();

		for (MapPoint[] point : e.getMapPoints()) {
			for (MapPoint p : point) {
				if (!p.isCenter()) {
					if (p.getClan() == null) {

						Location location = (new Location(e.getPlayer().getWorld(), (p.chunkPosition.x << 4), 110, (p.chunkPosition.z << 4))).add(7.0D, 0.0D, 7.0D);

						Optional<Region.Spawn> rg = Region.spawn().filter(reg -> reg instanceof Region.Spawn);

						if (rg.isPresent()) {
							if (rg.get().contains(location, 5)) {
								Region r = rg.get();
								if (!r.isPassthrough()) {
									p.setHover(StringUtils.use("&4Spawn").translate());
									p.setColor("&c");
									p.setRepresentation('⬛');
								}
							} else {
								Optional<Region> reg = Region.match(location).filter(r -> !(r instanceof Region.Spawn));
								if (reg.isPresent()) {
									Region r = reg.get();
									if (!r.isPassthrough()) {
										p.setHover(StringUtils.use("&2Region: &7" + r.getName()).translate());
										p.setColor("&2");
										p.setRepresentation('⬛');
									} else {
										if (e.getPlayer().isOp()) {
											if (!p.getColor().equals("&c")) {
												p.setColor("#d4d2cd");
												p.setRepresentation('⬜');
											}
										}
									}
								} else {
									if (!p.getColor().equals("&c") || !p.getColor().equals("&2")) {
										p.setHover(StringUtils.use("&4Wilderness").translate());
										p.setAppliance(() -> {
											Clan c = ClansAPI.getInstance().getClan(e.getPlayer().getUniqueId());
											if (c != null) {
												Claim claim = c.obtain(e.getPlayer().getWorld().getChunkAt(p.chunkPosition.x, p.chunkPosition.z));
												if (claim != null) {
													e.getPlayer().performCommand("c map");
													DefaultClan.action.sendMessage(e.getPlayer(), "&aChunk &6&7(&3X: &f" + claim.getChunk().getX() + " &3Z: &f" + claim.getChunk().getZ() + "&7) &ais now owned by our clan.");
												}
											}
										});
										p.setColor("&8");
										p.setRepresentation('⬜');
									}
								}
							}
						} else {
							Optional<Region> reg = Region.match(location).filter(r -> !(r instanceof Region.Spawn));
							if (reg.isPresent()) {
								Region r = reg.get();
								if (!r.isPassthrough()) {
									p.setHover(StringUtils.use("&2Region: &7" + r.getName()).translate());
									p.setColor("&2");
									p.setRepresentation('⬛');
								}
							} else {
								if (!p.getColor().equals("&c") || !p.getColor().equals("&2")) {
									p.setHover(StringUtils.use("&4Wilderness").translate());
									p.setColor("&8");
									p.setRepresentation('⬜');
									p.setAppliance(() -> {
										Clan c = ClansAPI.getInstance().getClan(e.getPlayer().getUniqueId());
										if (c != null) {
											Claim claim = c.obtain(e.getPlayer().getWorld().getChunkAt(p.chunkPosition.x, p.chunkPosition.z));
											if (claim != null) {
												e.getPlayer().performCommand("c map");
												DefaultClan.action.sendMessage(e.getPlayer(), "&aChunk &6&7(&3X: &f" + claim.getChunk().getX() + " &3Z: &f" + claim.getChunk().getZ() + "&7) &ais now owned by our clan.");
											}
										}
									});
								}
							}
						}
					} else {
						Clan c = p.getClan();
						if (!clans.contains(StringUtils.use(c.getColor() + c.getName()).translate())) {
							clans.add(StringUtils.use(c.getColor() + c.getName()).translate());
						}
						p.setRepresentation('⬛');
						p.setColor(c.getColor().replace("&l", ""));
					}
				} else {
					p.setRepresentation('❤');
				}
			}
		}

		List<String> bottom_new = new LinkedList<>();
		bottom_new.add(" ");
		bottom_new.add(StringUtils.use("&eNear by clans: &f{ " + String.join("&r, ", clans) + " &f}").translate());

		e.setAddedLinesBottom(bottom_new);

	}

}
