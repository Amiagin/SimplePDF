import com.blackwolf.pdf.SimplePDF;
import com.blackwolf.pdf.SimplePDF.Page;
import com.blackwolf.pdf.SimplePDF.PaperType;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Main {
	private static OutputStream outputStream = null;
	
    public static void main(String []args) {
        SimplePDF pdf = new SimplePDF();
        try {
        	createTestPage(pdf);
        	createPageOne(pdf);
//        	createInvoice(pdf);
        	createResume(pdf);
            pdf.writeTo(getOutputStream());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private static void createTestPage(SimplePDF pdf) {
    	Page page = pdf.createPage(PaperType.LETTER);
    	page.drawBitmap(1,1,"mark-zuckerberg-5.jpg",320,292);
    }
/*    
    private static void createPageOne(SimplePDF pdf) {
    	Page page = pdf.createPage(PaperType.LETTER);
        	
        page.setFont(32.0f);
        page.setTextColor(3,3,3);
        page.setText(1,1,"Mike Lau");
        	
        page.setFont(6.0f);
        page.setTextColor(0,0,255);
        page.setText(1,40,"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        page.setFont(10.0f);
        page.setTextColor(90,90,90);
        page.setText(1,100,"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        	
        page.setDrawColor(255,0,0);
        page.drawLine(10,page.getMargin()+10,page.getWidth()-page.getMargin());
        	
        page.setDrawColor(0,0,255);
        page.drawLine(10,page.getHeight()-page.getMargin()-10,page.getWidth()-page.getMargin());
        	
        page.setFillColor(210,0,0);
        page.drawRect(20,200,120,20);
        	
        page.setFillColor(0,210,0);
        page.drawRect(20,220,120,20);
        	
        page.setFillColor(0,0,210);
        page.drawRect(20,240,120,20);
    }
    
    private static void createInvoice(SimplePDF pdf) {
        Page page = pdf.createPage(PaperType.LETTER);
        page.drawCell(30,60,20,"Sample Invoice",0,Page.CENTER);
            
        page.setFont(12.0f);
        page.drawCell(30,120,20,"Invoice No.   :",0,Page.LEFT);
        page.drawCell(160,120,20,"INV00000000030",0,Page.LEFT);
            
        page.drawCell(30,140,20,"Customer Name :",0,Page.LEFT);
        page.drawCell(160,140,20,"Cris del Rosario",0,Page.LEFT);
            
        page.drawCell(30,160,20,"Purchase Date :",0,Page.LEFT);
        page.drawCell(160,160,20,"Friday, November 22, 2013 @ 01:46 AM",0,Page.LEFT);
            
        page.setFont(10.0f);
        page.drawCell(30,220,20,"Quantity",1,Page.LEFT);
        page.drawCell(180,220,20,"Description",1,Page.LEFT);
        page.drawCell(430,220,20,"Price",1,Page.LEFT);
            
        page.drawCell(30,240,20,"1",0,Page.LEFT);
        page.drawCell(180,240,20,"Classic Carry-all Tote Bag in Black",0,Page.LEFT);
        page.drawCell(430,240,20,"P 450.00",0,Page.RIGHT);

        page.setFont(10.0f);
        page.drawCell(30,290,20,"Discount",0,Page.LEFT);
        page.drawCell(430,290,20,"0.00",0,Page.RIGHT);
            
        page.drawCell(30,310,20,"Shipping Fee",0,Page.LEFT);
        page.drawCell(430,310,20,"0.00",0,Page.RIGHT);
            
        page.drawCell(30,330,20,"Tax",0,Page.LEFT);
        page.drawCell(430,330,20,"0.00",0,Page.RIGHT);
            
        page.setFont(18.0f);
        page.drawCell(30,530,20,"Total",0,Page.LEFT);
        page.drawCell(430,530,20,"P 450.00",0,Page.RIGHT);

        page.drawBitmap(1,1,"mark-zuckerberg-5.jpg",1000,914);    
    }
*/    
    private static void createResume(SimplePDF pdf) {
    	Page page = pdf.createPage(PaperType.LEGAL);
    	
    	page.setFillColor(6,6,6);
        page.drawRect(page.getMargin(),150,150,140);
    	
    	page.setFont("Arial","Bold",18.0f);
        page.setTextColor(3,3,3);
        page.setText(160,1,"Mike Klinton Lau");
        
        page.setFont("Arial","",12.0f);
        float y = 16;
        float gap = 6;
        page.setText(160,y,"09272478327");
        y += page.getFontSize() + gap;
        page.setText(160,y,"mikeklintonlau@gmail.com");
        y += page.getFontSize() + gap;
        page.setText(160,y,"Manila");
        y += page.getFontSize() + gap;
        page.setText(160,y,"Developer");
        
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,190,"OBJECTIVE");    
        page.setDrawColor(6,6,6);
        page.setFont("Arial","",12.0f);
        page.drawLine(page.getMargin(),216,page.getWidth()-(page.getMargin()*2));
        page.setText(6,210,"To apply for a job");
        
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,250,"EDUCATION");    
        page.setDrawColor(6,6,6);
        page.setFont("Arial","",12.0f);
        page.drawLine(page.getMargin(),276,page.getWidth()-(page.getMargin()*2));
        y = 270;
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,y,"De La Salle College of Saint Benilde");
        y += page.getFontSize() + gap;
        page.setFont("Arial","",12.0f);
        page.setText(6,y,"Information Systems");
        y += page.getFontSize() + gap;
        page.setText(6,y,"2014");

        page.setFont("Arial","Bold",12.0f);
        page.setText(6,350,"EXPERIENCE");    
        page.setDrawColor(6,6,6);
        page.setFont("Arial","",12.0f);
        page.drawLine(page.getMargin(),376,page.getWidth()-(page.getMargin()*2));
        y = 370;
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,y,"Deltek Systems Co Ltd");
        y += page.getFontSize() + gap;
        page.setFont("Arial","",12.0f);
        page.setText(6,y,"Intern");
        y += page.getFontSize() + gap;
        page.setText(6,y,"IT (09-16-2013 - 01-04-2014)");
        
        page.setDrawColor(200,200,200);
        page.drawLine(page.getMargin(),436,page.getWidth()-(page.getMargin()*2));
        y = 432;
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,y,"Entertainment Gateway Group");
        y += page.getFontSize() + gap;
        page.setFont("Arial","",12.0f);
        page.setText(6,y,"Junior Software Engineer");
        y += page.getFontSize() + gap;
        page.setText(6,y,"SMS (06-23-2014 - 09-15-2014)");
        
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,530,"SKILLS");    
        page.setDrawColor(6,6,6);
        page.setFont("Arial","",12.0f);
        page.drawLine(page.getMargin(),556,page.getWidth()-(page.getMargin()*2));
        y = 550;
        
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,y,"PHP");
        y += page.getFontSize() + gap;
        page.setFont("Arial","",12.0f);
        page.setText(6,y,"web development");
        y += page.getFontSize() + gap;
        
        page.setDrawColor(200,200,200);
        page.drawLine(page.getMargin(),y+20,page.getWidth()-(page.getMargin()*2));

        y += gap;
        
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,y,"Java");
        y += page.getFontSize() + gap;
        page.setFont("Arial","",12.0f);
        page.setText(6,y,"java programming");
        y += page.getFontSize() + gap;
        
        page.setDrawColor(200,200,200);
        page.drawLine(page.getMargin(),y+20,page.getWidth()-(page.getMargin()*2));
 
         y += gap;
        
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,y,"ASP");
        y += page.getFontSize() + gap;
        page.setFont("Arial","",12.0f);
        page.setText(6,y,"web development");
        y += page.getFontSize() + gap;
        
        page.setDrawColor(200,200,200);
        page.drawLine(page.getMargin(),y+20,page.getWidth()-(page.getMargin()*2));

        y += gap;
        
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,y,"HTML");
        y += page.getFontSize() + gap;
        page.setFont("Arial","",12.0f);
        page.setText(6,y,"front end developer");
        y += page.getFontSize() + gap;
        
        page.setDrawColor(200,200,200);
        page.drawLine(page.getMargin(),y+20,page.getWidth()-(page.getMargin()*2));
   
        y += gap;
        
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,y,"CSS");
        y += page.getFontSize() + gap;
        page.setFont("Arial","",12.0f);
        page.setText(6,y,"web design");
        y += page.getFontSize() + gap;
        
        page.setDrawColor(200,200,200);
        page.drawLine(page.getMargin(),y+20,page.getWidth()-(page.getMargin()*2));
    
        y += gap;
        
        page.setFont("Arial","Bold",12.0f);
        page.setText(6,y,"Android");
        y += page.getFontSize() + gap;
        page.setFont("Arial","",12.0f);
        page.setText(6,y,"mobile programming");
        y += page.getFontSize() + gap;
        
        page.setDrawColor(200,200,200);
        page.drawLine(page.getMargin(),y+20,page.getWidth()-(page.getMargin()*2));

    }
   
    private static OutputStream getOutputStream() throws Exception {
    	outputStream = new FileOutputStream(new File("sample.pdf"));
        return outputStream;
    }
}