import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.imageio.ImageIO;
import io.netty.handler.codec.http.*;
import io.netty.buffer.*;

if (object instanceof HttpRequest) {
	object.headers().set("Host", "image.freepik.com");
	object.headers().set("if-modified-since", "-1");
} else if (object instanceof FullHttpResponse) {
	if ("image/jpeg".equals(object.headers().getAndConvert(HttpHeaderNames.CONTENT_TYPE))) {
		object = object.copy();
		bb = object.content();
		if (bb.readableBytes() > 0) {
			bytes = new byte[bb.readableBytes()];
			bb.readBytes(bytes);
			bais = new ByteArrayInputStream(bytes);
			bi = ImageIO.read(bais);
			
			flipped = new BufferedImage(bi.getWidth(),bi.getHeight(),bi.getType());
			tran = AffineTransform.getTranslateInstance(0, bi.getHeight());
			flip = AffineTransform.getScaleInstance(1d, -1d);
			tran.concatenate(flip);
			g = flipped.createGraphics();
			g.setTransform(tran);
			g.drawImage(bi, 0, 0, null);
			g.dispose();
			
			baos = new ByteArrayOutputStream();
			ImageIO.write(flipped, "jpg", baos);
			newcontent = Unpooled.wrappedBuffer(baos.toByteArray());
			object = object.copy(false, newcontent);
		}
	}
}