package BaseDatos;

import com.barcodelib.barcode.QRCode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

public class EnviarMail extends Thread{

    private String numeroPedido;
    private String destinatario;
    private String datosQR;

    public EnviarMail(String destinatario, String datosQR){
        //  El destinatario sería el correo del proveedor
        String argumentos [] = datosQR.split("&");
        this.numeroPedido=argumentos[0];
        //this.destinatario=destinatario;
        this.destinatario="esauh97@gmail.com";
        this.datosQR = datosQR;
    }

    @Override
    public void run() {
        Properties props = new Properties();
        String archivo = "qrPedidoNº"+numeroPedido+".gif";
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");


        Session session = Session.getInstance(props);

        String correoRemitente="esaufdoexpress@gmail.com";
        String password="repqum-tycrud-Nyqzu0";
        String correoReceptor = destinatario;
        String asunto = "Etiqueta de envío pedido Nº"+numeroPedido;
        String mensaje= "Proceda a descargar y pegar la etiqueta de envío en el embalaje del pedido correspondiente.";
        generarQR(datosQR);
        try{
            BodyPart texto = new MimeBodyPart();
            BodyPart adjunto = new MimeBodyPart();
            texto.setText(mensaje);

            adjunto.setDataHandler(new DataHandler(new FileDataSource(archivo)));
            adjunto.setFileName("EtiquetaPedidoNº"+numeroPedido+".gif");
            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(texto);
            mimeMultipart.addBodyPart(adjunto);
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(correoRemitente));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(correoReceptor));
            mimeMessage.setSubject(asunto);
            mimeMessage.setContent(mimeMultipart);
            Transport transport = session.getTransport("smtp");
            transport.connect(correoRemitente,password);
            transport.sendMessage(mimeMessage,mimeMessage.getRecipients(Message.RecipientType.TO));
            transport.close();
            File ar = new File(archivo);
            ar.delete();
            //JOptionPane.showMessageDialog(null,"Correo enviado");

        }  catch(AddressException e) {
            System.out.println("Error en la direccion del correo");
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /*public void generarQR(String datos){
        int udm=0,resol=84,rot=0;
        float mi=0.000f,md=0.000f,ms=0.000f,min=0.000f, tam = 8.00f;
        try{
            QRCode c = new QRCode();
            c.setData(datos);
            c.setDataMode(QRCode.MODE_BYTE);
            c.setUOM(udm);
            c.setLeftMargin(mi);
            c.setRightMargin(md);
            c.setTopMargin(ms);
            c.setBottomMargin(min);
            c.setResolution(resol);
            c.setRotate(rot);
            c.setModuleSize(tam);

            String archivo = "qrPedidoNº"+numeroPedido+".gif";
            c.renderBarcode(archivo);

        }catch (Exception e){
            e.printStackTrace();
        }

    }*/
    public void generarQR(String matricula){
        String qrcode = "qrPedidoNº"+numeroPedido+".gif";
        QRCodeWriter writer = new QRCodeWriter();
        try{
            BitMatrix bitMatrix = writer.encode(matricula, BarcodeFormat.QR_CODE,200,200);
            Path path = FileSystems.getDefault().getPath(qrcode);
            MatrixToImageWriter.writeToPath(bitMatrix,"GIF",path);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
