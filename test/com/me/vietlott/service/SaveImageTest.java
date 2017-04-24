package com.me.vietlott.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;

/**
 * @author lamhm
 *
 */
public class SaveImageTest {
	private static final int TICKET_TYPE_MATRIC = 1;
	private static final int TICKET_TYPE_SEQUENCY = 2;
	private static final int TICKET_TYPE_KENO = 3;


	@Test
	public void saveImage() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			BufferedImage originalImage = ImageIO.read(new File("images/image.jpg"));

			// convert BufferedImage to byte array
			ImageIO.write(originalImage, "jpg", baos);
			baos.flush();

			byte[] imageInByte = baos.toByteArray();
			// convert byte array back to BufferedImage
			InputStream in = new ByteArrayInputStream(imageInByte);
			BufferedImage bImageFromConvert = ImageIO.read(in);

			// Graphics g = bImageFromConvert.getGraphics();
			// g.setFont(g.getFont().deriveFont(30f));
			// g.drawString("Hello World!", 100, 100);
			// g.dispose();

			ImageIO.write(bImageFromConvert, "jpg", new File("images/new-image.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void testTicket() {
		JsonObject ticketInfo = JsonObject.create();
		ticketInfo.put("ticket_type", 1);

		JsonArray tickets = JsonArray.create();
		for (int i = 0; i < 1; i++) {
			JsonObject ticket = JsonObject.create();
			ticket.put("ticket_numer", "1,2,3,4,5,20");
			ticket.put("easy_pick", false);
			ticket.put("money_bet", 12);
			tickets.add(ticket);
		}

		ticketInfo.put("list_ticket", tickets);
		System.out.println(ticketInfo.toString());
		Assert.assertTrue(validateOrderTicketData(0, ticketInfo.toString()));

	}


	private boolean validateOrderTicketData(int userId, String ticketData) {
		if (StringUtils.isBlank(ticketData)) {
			return false;
		}

		try {
			JsonObject ticketObj = JsonObject.fromJson(ticketData);
			Integer ticketType = ticketObj.getInt("ticket_type");
			if (ticketType == null || (ticketType < 1 || ticketType > 3)) {
				return false;
			}

			JsonArray listTicket = ticketObj.getArray("list_ticket");
			if (listTicket == null || listTicket.size() < 1) {
				return false;
			}

			for (int i = 0; i < listTicket.size(); i++) {
				JsonObject ticket = listTicket.getObject(i);
				if (!validateTicket(ticketType, ticket)) {
					return false;
				}

			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}


	private boolean validateTicket(int ticketType, JsonObject ticket) {
		String ticketNumber = ticket.getString("ticket_numer");

		switch (ticketType) {
		case TICKET_TYPE_MATRIC:
			if (StringUtils.isBlank(ticketNumber)) {
				return false;
			}

			// kiểu ma trận phải đủ 6 số
			String[] numbers = StringUtils.split(ticketNumber, ",");
			if (numbers == null || numbers.length < 6) {
				return false;
			}

			// gửi lên là số
			for (String number : numbers) {
				if (!StringUtils.isNumeric(number)) {
					return false;
				}
			}

			break;

		case TICKET_TYPE_SEQUENCY:
			if (StringUtils.isBlank(ticketNumber)) {
				return false;
			}

			// kiểu dãy số phải đủ 4 số
			numbers = StringUtils.split(ticketNumber, ",");
			if (numbers == null || numbers.length < 4) {
				return false;
			}

			break;

		case TICKET_TYPE_KENO:

			break;

		default:
			break;
		}

		return true;
	}
}
