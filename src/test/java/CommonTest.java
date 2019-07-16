import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CommonTest {

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("D:\\JetBrains\\IntelliJ IDEA 2018.3\\workspace\\demos\\photoview" +
                "\\src\\main\\resources\\component\\ImagePane\\back_button.png"));

        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                int pixel = image.getRGB(i, j);
                if (pixel != 0) {
                    int a = (pixel >> 24) & 0xff;
                    int r = (pixel >> 16) & 0xff;
                    int g = (pixel >> 8) & 0xff;
                    int b = pixel & 0xff;
                    System.out.print(a + "  ");
                    System.out.print(r + "  ");
                    System.out.print(g + "  ");
                    System.out.print(b + "           ");

                    pixel = (g & 0x000000ff) | (pixel & 0xffffff00); //用r的值设置b的值
                    pixel = ((g<<8) & 0x0000ff00) | (pixel & 0xffff00ff);//用r的值设置g的值
                    image.setRGB(i, j, pixel);
                }
            }
            System.out.println();
        }

        ImageIO.write(image, "png", new File("g:/TestFolder/test.jpg"));
    }

}
