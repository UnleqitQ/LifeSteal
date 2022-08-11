package me.unleqitq.lifesteal.storage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.unleqitq.lifesteal.LifeSteal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileStorage implements IStorage {
	
	File file;
	Map<UUID, Integer> dataMap = new HashMap<>();
	
	public FileStorage() {
		file = new File(LifeSteal.getInstance().getDataFolder(), "data");
	}
	
	@Override
	public int getHearts(UUID player) {
		return dataMap.getOrDefault(player, 10);
	}
	
	@Override
	public void setHearts(UUID player, int amount) {
		dataMap.put(player, amount);
	}
	
	@Override
	public void load() {
		if (!file.exists())
			return;
		ByteBuf buffer;
		try {
			buffer = Unpooled.wrappedBuffer(Files.readAllBytes(file.toPath()));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		while (buffer.readableBytes() > 0) {
			long msb = buffer.readLong();
			long lsb = buffer.readLong();
			int amount = buffer.readShort();
			dataMap.put(new UUID(msb, lsb), amount);
		}
	}
	
	@Override
	public void save() {
		ByteBuf buffer = Unpooled.buffer();
		for (Map.Entry<UUID, Integer> entry : dataMap.entrySet()) {
			buffer.writeLong(entry.getKey().getMostSignificantBits());
			buffer.writeLong(entry.getKey().getLeastSignificantBits());
			buffer.writeShort(entry.getValue());
		}
		try {
			Files.write(file.toPath(), buffer.array(), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
