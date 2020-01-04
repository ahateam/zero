package zyxhj.utils;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码工具类<br>
 * Java后端的二维码生成与解析工具<br><br>
 * 二维码生成：produceQRCode(String, String, String, boolean)<br><br>
 * 二维码解析：analysisQRCode(String)
 * 
 * @author JXians
 * @version 1.0.0
 *
 */
public class QRCodeUtils {

	public static class BufferedImageLuminanceSource extends LuminanceSource {

		private final BufferedImage image;
		private final int left;
		private final int top;

		public BufferedImageLuminanceSource(BufferedImage image) {
			this(image, 0, 0, image.getWidth(), image.getHeight());
		}

		public BufferedImageLuminanceSource(BufferedImage image, int left, int top, int width, int height) {
			super(width, height);

			int sourceWidth = image.getWidth();
			int sourceHeight = image.getHeight();
			if (left + width > sourceWidth || top + height > sourceHeight) {
				throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
			}

			for (int y = top; y < top + height; y++) {
				for (int x = left; x < left + width; x++) {
					if ((image.getRGB(x, y) & 0xFF000000) == 0) {
						image.setRGB(x, y, 0xFFFFFFFF); // = white
					}
				}
			}

			this.image = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_BYTE_GRAY);
			this.image.getGraphics().drawImage(image, 0, 0, null);
			this.left = left;
			this.top = top;
		}

		public byte[] getRow(int y, byte[] row) {
			if (y < 0 || y >= getHeight()) {
				throw new IllegalArgumentException("Requested row is outside the image: " + y);
			}
			int width = getWidth();
			if (row == null || row.length < width) {
				row = new byte[width];
			}
			image.getRaster().getDataElements(left, top + y, width, 1, row);
			return row;
		}

		public byte[] getMatrix() {
			int width = getWidth();
			int height = getHeight();
			int area = width * height;
			byte[] matrix = new byte[area];
			image.getRaster().getDataElements(left, top, width, height, matrix);
			return matrix;
		}

		public boolean isCropSupported() {
			return true;
		}

		public LuminanceSource crop(int left, int top, int width, int height) {
			return new BufferedImageLuminanceSource(image, this.left + left, this.top + top, width, height);
		}

		public boolean isRotateSupported() {
			return true;
		}

		public LuminanceSource rotateCounterClockwise() {
			int sourceWidth = image.getWidth();
			int sourceHeight = image.getHeight();
			AffineTransform transform = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, sourceWidth);
			BufferedImage rotatedImage = new BufferedImage(sourceHeight, sourceWidth, BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g = rotatedImage.createGraphics();
			g.drawImage(image, transform, null);
			g.dispose();
			int width = getWidth();
			return new BufferedImageLuminanceSource(rotatedImage, top, sourceWidth - (left + width), getHeight(),
					width);
		}

	}

	public static class QRCodeUtil {

		private static final String CHARSET = "utf-8";
		private static final String FORMAT_NAME = "JPG";
		// 二维码尺寸
		private static final int QRCODE_SIZE = 300;
		// LOGO宽度
		private static final int WIDTH = 60;
		// LOGO高度
		private static final int HEIGHT = 60;

		private static BufferedImage createImage(String content, String imgPath, boolean needCompress)
				throws Exception {
			Hashtable hints = new Hashtable();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			hints.put(EncodeHintType.CHARACTER_SET, CodecUtils.CHARSET_UTF8);
			hints.put(EncodeHintType.MARGIN, 1);
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE,
					QRCODE_SIZE, hints);
			int width = bitMatrix.getWidth();
			int height = bitMatrix.getHeight();
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
				}
			}
			if (imgPath == null || "".equals(imgPath)) {
				return image;
			}
			// 插入图片
			QRCodeUtil.insertImage(image, imgPath, needCompress);
			return image;
		}

		private static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws Exception {
			File file = new File(imgPath);
			if (!file.exists()) {
				System.err.println("" + imgPath + "   该文件不存在！");
				return;
			}
			Image src = ImageIO.read(new File(imgPath));
			int width = src.getWidth(null);
			int height = src.getHeight(null);
			if (needCompress) { // 压缩LOGO
				if (width > WIDTH) {
					width = WIDTH;
				}
				if (height > HEIGHT) {
					height = HEIGHT;
				}
				Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
				BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = tag.getGraphics();
				g.drawImage(image, 0, 0, null); // 绘制缩小后的图
				g.dispose();
				src = image;
			}
			// 插入LOGO
			Graphics2D graph = source.createGraphics();
			int x = (QRCODE_SIZE - width) / 2;
			int y = (QRCODE_SIZE - height) / 2;
			graph.drawImage(src, x, y, width, height, null);
			Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
			graph.setStroke(new BasicStroke(3f));
			graph.draw(shape);
			graph.dispose();
		}

		private static void mkdirs(String destPath) {
			File file = new File(destPath);
			// 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
			if (!file.exists() && !file.isDirectory()) {
				file.mkdirs();
			}
		}

		/**
		 * 解析二维码
		 * 
		 * @param file File二维码文件对象
		 * @return
		 * @throws Exception
		 */
		protected static String analysisQRCode(File file) throws Exception {
			BufferedImage image;
			image = ImageIO.read(file);
			if (image == null) {
				return null;
			}
			BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Result result;
			Hashtable hints = new Hashtable();
			hints.put(DecodeHintType.CHARACTER_SET, CodecUtils.CHARSET_UTF8);
			result = new MultiFormatReader().decode(bitmap, hints);
			String resultStr = result.getText();
			return resultStr;
		}

		/**
		 * 解析二维码
		 * 
		 * @param path 文件路径
		 * @return
		 * @throws Exception
		 */
		public static String analysisQRCode(String path) throws Exception {
			return QRCodeUtil.analysisQRCode(new File(path));
		}

		/**
		 * 二维码中嵌入图标
		 * 
		 * @param content      二维码信息
		 * @param imgPath      嵌入二维码的图片地址
		 * @param destPath     二维码存放地址
		 * @param needCompress 是否压缩
		 * @throws Exception
		 */
		public static void produceQRCode(String content, String imgPath, String destPath, boolean needCompress)
				throws Exception {
			BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
			mkdirs(destPath);
			ImageIO.write(image, FORMAT_NAME, new File(destPath));
		}

		/**
		 * 二维码生成，不压缩
		 * 
		 * @param content  二维码信息
		 * @param imgPath  嵌入二维码的图片地址
		 * @param destPath 二维码存放地址
		 * @throws Exception
		 */
		public static void produceQRCode(String content, String imgPath, String destPath) throws Exception {
			QRCodeUtil.produceQRCode(content, imgPath, destPath, false);
		}

		/**
		 * 二维码生成，不嵌入图标，不压缩文件
		 * 
		 * @param content  二维码信息
		 * @param destPath 二维码存放地址
		 * @throws Exception
		 */
		public static void produceQRCode(String content, String destPath) throws Exception {
			QRCodeUtil.produceQRCode(content, null, destPath, false);
		}

		/**
		 * 生成二维码
		 * 
		 * @param content      二维码信息
		 * @param imgPath      嵌入二维码的图片地址
		 * @param needCompress 是否压缩
		 * @return
		 * @throws Exception
		 */
		protected static BufferedImage produceQRCode(String content, String imgPath, boolean needCompress)
				throws Exception {
			BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
			return image;
		}

		protected static void produceQRCode(String content, String imgPath, OutputStream output, boolean needCompress)
				throws Exception {
			BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
			ImageIO.write(image, FORMAT_NAME, output);
		}

		protected static void produceQRCode(String content, OutputStream output) throws Exception {
			QRCodeUtil.produceQRCode(content, null, output, false);
		}

	}

	public static void main(String[] args) throws Exception {
		// 存放在二维码中的内容
		String text = "https://www.baidu.com";
		// 嵌入二维码的图片路径
		String imgPath = "G:/qrCode/dog.jpg";
		// 生成的二维码的路径及名称
//		String destPath = "C:\\Users\\Admin\\Desktop\\文档\\集体经济\\图片\\名片\\102.jpg";
		String destPath = "C:\\Users\\Admin\\Desktop\\微信图片_20191026143756.jpg";
		// 生成二维码
		QRCodeUtil.produceQRCode(text, null, destPath, false);
		// 解析二维码
		String str = QRCodeUtil.analysisQRCode(destPath);
		// 打印出解析出的内容
		System.out.println(str);

	}

	// 定时器
	public static void showDayTime() {
//		Calendar calendar = Calendar.getInstance();
//		int year = calendar.get(Calendar.YEAR);
//		int month = calendar.get(Calendar.MONTH);
//		int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//		calendar.set(2019, 10, 22, 9, 56, 00);// 设置要执行的日期时间

//		Date defaultdate = calendar.getTime();

		Date defaultdate = new Date();

		Timer dTimer = new Timer();
		dTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("每日任务已经执行");
			}
		}, defaultdate, 24 * 60 * 60 * 1000);// 24* 60* 60 * 1000
	}

}
