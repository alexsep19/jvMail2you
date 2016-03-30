package jv.jvMail2you;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * Hello world!
 *
 */
public class App {
	final static String ConfFileName = "conf.properties";
//	ResourceBundle m_appResources = null;
    Properties conf = new Properties();

    public static void main( String[] args )
    {
    	 App app = new App();
//     	 app.setPass("pass");
         try {
             app.sendSimpleMail();
             System.out.println( "success" );
         } catch (Exception e) {
             System.out.println(e.getMessage());
             e.printStackTrace();
             System.out.println( "fail" );
         }
    }
    
    public void sendSimpleMail() throws Exception {//TODO:правильные Exception
        Properties props = new Properties();
        
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("mySecretPassword"); 
        
        conf.load(new FileInputStream(ConfFileName));
//        conf.setProperty("mail.password", encryptor.encrypt("8"));
//        conf.store(new FileOutputStream(ConfFileName), null);
        
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.allow8bitmime", "true");
        props.setProperty("mail.host", conf.getProperty("mail.smtp.host"));
        props.setProperty("mail.port", conf.getProperty("mail.smtp.port"));
//        props.setProperty("mail.user", conf.getProperty("mail.user"));
//        props.setProperty("mail.password", conf.getProperty("mail.password"));
        props.put("mail.smtp.starttls.enable", "true");
        
//        Session mailSession = Session.getDefaultInstance(props, null);//javax.mail.Session
        Session mailSession = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                  protected PasswordAuthentication getPasswordAuthentication() {
                      return new PasswordAuthentication(conf.getProperty("mail.user"), encryptor.decrypt(conf.getProperty("mail.password")));
                }
              });
        
        
//        mailSession.setDebug(true);
//        fetchMessages (mailSession, "INTRAMAIL", "t01335", "bel123");
        
        Transport transport = mailSession.getTransport();
        
        MimeMessage message = new MimeMessage(mailSession);
        
        message.setFrom(new InternetAddress(conf.getProperty("mail.user")));
        
        //необходимо для русского языка в "теме" письма (подошло также UTF-8):
        message.setSubject(conf.getProperty("message.theme"));
        //или:
        //message.setText(m_appResources.getString("message.body"), "UTF-8");
        message.setContent( "ssыыыыыы",
                "text/plain;charset=UTF-8");//не захотело UTF-16
        
        /*
         String Html = ...;
         String DefaultCharSet=
        MimeUtility.getDefaultJavaCharset();
        msg.setText(MimeUility.encodeText(Html,DefaultCharSet,"B"));
        msg.send();
         
        Q short for Quoted Printable
        B short for Base64
         */
        
        InternetAddress addrTo = new InternetAddress(conf.getProperty("recipient.address"));
        //для русского языка в имени получателя (аналогично в имени отправителя): 
//        try {
//            addrTo.setPersonal("заец зайцев", "KOI8-R");
//        } catch (java.io.UnsupportedEncodingException uee) {
//            System.out.println(uee.getMessage());
//        }
        message.addRecipient(Message.RecipientType.TO,
                addrTo //Например:"=?koi8-r?Q?=E0=D2=C7=C9=CE_=E1=2E=EB=2E?=" - эти закорючки означают "Юргин А.К."
                );
        // Проверка задания натурального заголовка (работает, проще всего увидеть,задав mailSession.setDebug(true)):
        message.setHeader("X-Mailer", "org.sukhoi.mail"); //произвольное указание почтовой программы
        
        message.setSentDate(new Date());
//        message.setHeader("Content-Transfer-Encoding", "base64"); //должно быть после setText()

        transport.send(message);
//        transport.connect();
        //нужен поток, т.к. возможна долгая операция.
        //сравнить с send();
        //разрешить частичную посылку, если адресов много,чтоб при одном неправильном адресе
        //не прекращало работу.
        
//        transport.sendMessage(message,
//                message.getRecipients(Message.RecipientType.TO));
        
        //для отладки - список заголовков:
//        Enumeration msgHeaders = message.getAllHeaders();
//        System.out.println("HEADERS");
//        while (msgHeaders.hasMoreElements()) {
//            Header header = (Header) msgHeaders.nextElement();
//            System.out.println(header.getName() + "=" + header.getValue());
//        }
        
        transport.close();
    }

    void setPass(String pass){
      StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();     
      encryptor.setPassword("jasypt");
    }
    
//    private void loadconf(String fileName) {
//    	
//        m_appResources = PropertyResourceBundle.getBundle(resourceName);//TODO:использовать только для многоязычных сообщений
//        //для отладки
//        Enumeration keys = m_appResources.getKeys();
//        while (keys.hasMoreElements()) {
//            String key = (String) keys.nextElement();
//            System.out.println(key + "=" + m_appResources.getString(key));
//        }
//    }

}
