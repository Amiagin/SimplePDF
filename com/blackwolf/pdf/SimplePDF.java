package com.blackwolf.pdf;

/* 
 * @author Cris del Rosario
 * @version 0.1
 *
 * Developed by Cris del Rosario
 *
 * Description                    : PDF Writer/generator
 *
 * Creation/Modification History  :
 *                                   2014.09.17 - Initial creation
 *
 * Example:
 *
 * SimplePDF pdf = new SimplePDF();
 * Page page = pdf.createPage(PaperType.LETTER,Page.ORIENTATION_LANDSCAPE,1);
 * page.setText(0,0,"This is a test");
 * pdf.writeTo(getOutputStream());
 *
 */
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.text.SimpleDateFormat;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.StringBuffer;
import java.security.MessageDigest;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
 
public class SimplePDF {
	private List<Page>pages = null;
	private String author;
	private String creator;
	private String title;
	private String producer;
	private String ID;
	
	private int objectID;
	private int rootObject;
	private int offset;
	private int catalog;
	private int numPages;
	private int fontOffset;
	private int parentOffset;
	private int pageOffsetStart;
    private int pageNumber;
	
	private OutputStream outputStream = null;
	private List<Integer>objectSet = null;
	private List<Integer>xrefs = null;
	private HashMap<String,XObject>imageSet;
	private int imageID;
	
	public static final float VERSION = 0.1f;
	public static final String TAG = "SimplePDF";
	
	private HashMap<String,Integer>currentFonts = null;
	private List<String>fontSet =  null;
    private int fontID;
	
    public SimplePDF() {
    	this.pages = new ArrayList<Page>();
    	
    	this.author = TAG;
    	this.creator = TAG + " " + VERSION;
    	this.title = TAG + " Creator";
    	this.producer = TAG + "SimplePDF";
    	this.ID = "";
    	
    	this.objectID = 0;
    	this.rootObject = 0;
    	this.offset = 0;
    	this.catalog = 0;
    	this.numPages = 0;
    	this.fontOffset = 0;
    	this.parentOffset = 0;
    	
    	this.pageOffsetStart = 0;
    	this.pageNumber = 0;
    	
    	this.objectSet = new ArrayList<Integer>();
    	this.xrefs = new ArrayList<Integer>();
    	
    	this.imageSet = new HashMap<String,XObject>();
    	this.imageID = 0;
    	
    	this.currentFonts = new HashMap<String,Integer>();
        this.fontID = 0;
        
        this.fontSet = new ArrayList<String>();
    }
        
    public Page createPage(int paperType) {
    	return createPage(paperType,Page.ORIENTATION_PORTRAIT,++pageNumber);
    }
    
    public Page createPage(int paperType,int pageNumber) {
    	return createPage(paperType,Page.ORIENTATION_PORTRAIT,pageNumber);
    }
     
    public Page createPage(int paperType,int orientation,int pageNumber) {
         float pageWidth = 0, pageHeight = 0;
         
         if (paperType == PaperType.LETTER) {
         	pageWidth = 612;
         	pageHeight = 792;
         } else if (paperType == PaperType.LEGAL) {
         	pageWidth = 612;
         	pageHeight = 1008;
         } else if (paperType == PaperType.A4) {
         	pageWidth = 595.28f;
         	pageHeight = 841.89f;
         } else {
         	pageWidth = 612;
         	pageHeight = 792;
         }
                  
         if (orientation == Page.ORIENTATION_LANDSCAPE) {
             float width = pageWidth;
             
             pageWidth = pageHeight;
             pageHeight = width;
         }
                  
         return createPage(pageWidth,pageHeight,pageNumber);
    }
     
    public Page createPage(float pageWidth,float pageHeight, int pageNumber) {
    	Page page = new Page(pageWidth,pageHeight,pageNumber);
    	
    	page.setData("2 J\n");
    	page.setData(page.getLineWidth() + " w\n");
    	page.setDefaultFont();
    	
    	pages.add(page);
    	
    	numPages++;
    	return pages.get(numPages-1);
    }
    
    public void writeTo(OutputStream outputStream) throws IOException {
    	if (outputStream != null) {
    		setOutputStream(outputStream);
    		
    		writePDFHeader();
    		writeBaseFont();
    		writeBitmaps();
    		writeStreams();
    		writePages();
    		writeCatalog();
    		writeXREF();
    		writeTrailer();
    		
    		closeOutputStream();
    	}
    }
    
    private void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    private OutputStream getOutputStream() {
        return outputStream;
    }
    
    private void closeOutputStream() throws IOException {
        outputStream.flush();
        outputStream.close();
    }
    
    private void writeData(String value) {
    	try {
    	    outputStream.write(value.getBytes());
    	    offset += value.length();
    	} catch (Exception exception) {
    		exception.printStackTrace();
    	}
    }
    
    private void writeData(int []values) {
        try {
        	for (int value:values) {
        	    outputStream.write(value);
        	}
        	offset += values.length;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private void writeData(byte []values) {
        try {
        	outputStream.write(values);
        	offset += values.length;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private void writeData(byte value) {
    	try {
    		outputStream.write(value);
    		offset++;
    	} catch (Exception exception) {
    	    exception.printStackTrace();
    	}
    }
    
    private void writePDFHeader() {
        writeData("%PDF-1.4\n");
        writeData(new int[]{0x25,0xe2,0xe3,0xcf,0xd3});
    	writeData("\n");
    		
    	writeStartBlock();
    	writeAuthor();
    	writeCreator();
    	writeTitle();
    	writeProducer();
    	writeCreationDate();
    	writeEndBlock();
    }
    
    private void writeStartBlock() {
    	objectSet.add(++objectID);
    	xrefs.add(offset);
    	writeData(objectID + " 0 obj\n<<");
    }
    
    private void writeEndBlock() {
    	writeData("\n>>\nendobj\n");
    }
    
    private void writeAuthor() {
    	if (author != null || !author.equals("")) {
    	    writeData("/Author (" + escapeString(author) + ")\n");
    	}
    }
    
    private void writeCreator() {
    	if (creator != null || !creator.equals("")) {
    	    writeData("/Creator (" + escapeString(creator) + ")\n");
    	}
    }
    
    private void writeTitle() {
    	if (title != null || !title.equals("")) {
    	    writeData("/Title (" + escapeString(title) + ")\n");
    	}
    }
    
    private void writeProducer() {
    	if (producer != null && !producer.equals("")) {
    	    writeData("/Producer (" + escapeString(producer) + ")\n");
    	}
    }
    
    private void writeCreationDate() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        writeData("/CreationDate (D:" + dateFormat.format(new Date()) + "Z)\n");
    }
    
    private void writeBaseFont() {
    	if (!currentFonts.isEmpty()) {
    	    Iterator i = currentFonts.entrySet().iterator();
    	    while (i.hasNext()) {
    	    	Map.Entry entry = (Map.Entry)i.next();
    	    	
    	    	writeStartBlock();
    	        writeData("/Type /Font\n/Subtype /Type1\n/BaseFont /" + entry.getKey() + " /Encoding /WinAnsiEncoding");
    	        writeEndBlock();
    	        
    	        fontSet.add("/F" + entry.getValue() + " " + objectID + " 0 R");
    	
    	        fontOffset = objectID;
    	    }
    	}    	
    }
    
    private void writeStreams() {
    	int pageIdx = 0;
    	while (pageIdx < numPages) {
    		writeStream(pages.get(pageIdx).getContents());
    		pages.get(pageIdx).setOffset(objectID);
    		pageIdx++;
    	}
    }
    
    private void writeStream(String value) {
    	pageOffsetStart = objectID;
    	ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    	OutputStreamWriter outputWriter = null;
    	
    	try {
    	    outputWriter = new OutputStreamWriter(new DeflaterOutputStream(byteOutput));
    	    outputWriter.write(value);
    	    outputWriter.close();
    	   
    	    xrefs.add(offset);
    	   
    	    writeData((++objectID) + " 0 obj<< /Filter /FlateDecode /Length " + byteOutput.size() + ">>");
            writeData("stream\n");
            writeData(byteOutput.toByteArray());
            writeData("\nendstream\n");
            writeData("endobj\n");
            
            objectSet.add(objectID);
    	} catch (Exception exception) {
    	}
    }
    
    private void writeBitmaps() {
    	Iterator i = imageSet.entrySet().iterator();
    	while (i.hasNext()) {
    	    Map.Entry entry = (Map.Entry)i.next();
    	    writeBitmap(imageSet.get(entry.getKey()));
    	}
    }
    
    private void writeBitmap(XObject image) {
    	xrefs.add(offset);
    	
        writeData((++objectID) + " 0 obj<</Type /XObject\n/Subtype /Image /Width " + image.imageWidth + " /Height " + image.imageHeight + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length " + image.imageData.length + ">>");
        writeData("stream\n");
        writeData(image.imageData);
        writeData("\nendstream\n");
        writeData("endobj\n");
        
        objectSet.add(objectID);
        
        image.objectID = objectID;
    }
    
    private String getImageSet() {
    	StringBuffer images = new StringBuffer();
    	Iterator i = imageSet.entrySet().iterator();
    	while (i.hasNext()) {
    	    Map.Entry entry = (Map.Entry)i.next();
    	    XObject image = imageSet.get(entry.getKey());
    	    images.append("/I" + image.imageID + " " + image.objectID + " 0 R\n");
    	}
    	return images.toString();
    }
    
    private String getFontSet() {
        StringBuffer fonts = new StringBuffer();
        for (String font:fontSet) {
            fonts.append(font + "\n");
        }
        return fonts.toString();
    }
    
    private void writePages() {
        StringBuffer kids = new StringBuffer();
        
        int objectIdx = objectID + 1;
        int pageIdx = 0;
        String space = "";
        
        while (pageIdx < numPages) {
        	writeStartBlock();
        	Page page = pages.get(pageIdx);
        	writeData("/Type /Page\n/Parent " + (objectIdx + numPages) + " 0 R\n/MediaBox [0 0 " + page.getWidth() + " " + page.getHeight() + "] /Contents " + page.getOffset() + " 0 R /Resources << /ProcSet [/PDF /Text /ImageB /ImageC /ImageI]\n/Font <<\n" + getFontSet() + ">>\n/XObject <<\n" + getImageSet() + ">> >>\n");
        	writeEndBlock();
        	
        	kids.append(space);
        	kids.append(objectID + " 0 R");
        	space = " ";
        	pageIdx++;
        }
        
        writeStartBlock();
        writeData("/Type /Pages /Kids[" + kids.toString() + "] /Count " + numPages);
        writeEndBlock();
        
        catalog = objectID;
        parentOffset = catalog;
    }
    
    private void writeCatalog() {
    	writeStartBlock();
    	writeData("/Type /Catalog /Pages " + catalog + " 0 R /PageLayout /OneColumn");
    	writeEndBlock();
    	
    	rootObject = objectID;
    }
    
    private void writeXREF() {
    	writeData("xref\n");
    	writeData("0 " + (objectID+1) + "\n");
    	writeData("0000000000 65535 f\n");
    	
    	for (Object offset : xrefs.toArray()) {
    	    writeData(padZero((Integer)offset) + " 00000 n\n");
    	}
    }
    
    private void writeTrailer() {
    	writeData("trailer\n");
    	writeData("<</Size " + (objectID + 2));
    	writeData("/Root " + rootObject + " 0 R");
        writeData("/Info 1 0 R");
        
        generateID();
        writeData("/ID[<" + this.ID + "><" + this.ID + ">]>>\n");
        
        writeData("startxref\n");
        writeData(offset + "\n");
        writeData("%%EOF\n");
    }
    
    private void generateID() {
    	MessageDigest digest = null;
    	String cmap = "0123456789ABCDEF";
    	try {
    		digest = MessageDigest.getInstance("MD5");
    		byte []digestBytes = digest.digest(this.producer.getBytes());
    		StringBuffer stringBuffer = new StringBuffer();
    		int index = 0;
    		while (index < digestBytes.length) {
    			stringBuffer.append(cmap.charAt((digestBytes[index] & 0xf0) >> 4));
                stringBuffer.append(cmap.charAt(digestBytes[index] & 0x0f));

    		    index++;
    		}
    		this.ID = stringBuffer.toString();
    	} catch (Exception exception) {
    		exception.printStackTrace();
    	}
    }
    
    public String escapeString(String value) {
        int length = value.length();
        int index = 0;
        StringBuffer escapedString = new StringBuffer();
        while (index < length) {
            if (value.charAt(index) == ')') {
            	escapedString.append("\\");
            } else if (value.charAt(index) == '(') {
            	escapedString.append("\\");
            }
            escapedString.append(value.charAt(index));
            index++;
        }
        return escapedString.toString();
    }
    
    public String padZero(int value) {
        String output = "" + value;
        int index = output.length();
        while (index < 10) {
        	output = "0" + output;
            index++;
        }
        return output;
    }
        
    public final class Page {
    	public static final int ORIENTATION_PORTRAIT = 0;
    	public static final int ORIENTATION_LANDSCAPE = 1;

        public static final int LEFT = 0;
        public static final int RIGHT = 1;
        public static final int CENTER = 2;

        private float pageWidth, pageHeight;
        private int pageNumber;
        private int pageOffset;
        private float lineWidth;
        private float pageMargin;
        private float pageLeftMargin,pageTopMargin,pageRightMargin,pageBottomMargin;
        private StringBuffer pageContent;
        
        private float scale = 72/25.4f; // mm
        
        private float textColor;
        private String fontFamily;
        private String fontStyle;
        private float fontSize;
        private String []fonts = {
        	"Courier","Courier-Bold","Courier-BoldOblique","Courier-Oblique",
        	"Helvetica","Helvetica-Bold","Helvetica-BoldOblique","Helvetica-Oblique",
        	"Times-Roman","Times-Bold","Times-BoldItalic","Times-Italic"
        };
        
        private float xpos, ypos;
                 
        public Page(float pageWidth,float pageHeight,int pageNumber) {
            this.pageWidth = pageWidth;
            this.pageHeight = pageHeight;
            this.pageNumber = pageNumber;
            
            this.pageMargin = 28.35f/scale;
            this.pageLeftMargin = this.pageMargin;
            this.pageRightMargin = this.pageMargin;
            this.pageTopMargin = this.pageMargin;
            this.pageBottomMargin = this.pageMargin;
            
            this.pageContent = new StringBuffer();
            
            this.textColor = 0.0f;
            this.fontSize = 10.0f/scale;
            this.fontFamily = "Helvetica";
            this.fontStyle = "Bold";
            this.lineWidth = 0.567f/scale;
            
            this.xpos = pageLeftMargin;
            this.ypos = pageTopMargin; 
        }
        
        public float getX() {
            return xpos;
        }
        
        public float getY() {
            return ypos;
        }
        
        public void setX(float x) {
            xpos = x;
        }
        
        public void setY(float y) {
            ypos = y;
        }
        
        public void setPosition(float x, float y) {
            xpos = x;
            ypos = y;
        }
        
        public void setText(String value) {
            setText(xpos,ypos,value);
            
            if (ypos < pageHeight-(pageBottomMargin+pageTopMargin)) {
                ypos += fontSize;
            }
        }
        
        public void setText(float x, float y,String value) {
        	float dx = x + pageLeftMargin;
        	float dy = (pageHeight - (pageTopMargin+pageBottomMargin)) - y;
        	
        	setData("");
        	
        	if ((value.length()*(fontSize/2)) > (pageWidth-(pageMargin*2))) {
        		int index = 0;
        		int size = (int)((pageWidth-(pageMargin*2))/(fontSize/2));
        		int cy = 0;
        		while (index < value.length()) {
                    if ((index+size) > value.length()) {
                    	size = (value.length()-index);
                    }
        			setData("BT " + dx + " " + (dy-cy) + " Td (" + escapeString(value.substring(index,index+size)) + ") Tj ET\n");
        			cy+=fontSize*scale;
        			index += size;
        		}
        	} else {
        		setData("BT " + dx + " " + dy + " Td (" + escapeString(value) + ") Tj ET\n");
        	}
        }
        
        public void setDrawColor(int r, int g, int b) {
            setData(((float)r/255) + " " + ((float)g/255) + " " + ((float)b/255) + " RG\n");
        }
        
        public void setFillColor(int r, int g, int b) {
        	setData(((float)r/255) + " " + ((float)g/255) + " " + ((float)b/255) + " rg\n");
        }
                
        public void setTextColor(int r, int g, int b) {
        	setData(((float)r/255) + " " + ((float)g/255) + " " + ((float)b/255) + " rg\n");
        }
        
        public void drawLine(float x, float y, float size) {
        	drawLine(x,y,x+size,y);
        }

        public void drawLine(float x1, float y1, float x2, float y2) {
        	setData(x1 + " " + (pageHeight-y1) + " m " + (x2) + " " + (pageHeight-y2) + " l S\n");
        }
        
        public void setLineWidth(float lineWidth) {
            setData(lineWidth + " w\n");
        }
        
        public void drawRect(float x, float y, float width, float height) {
        	setData(x + " " + (pageHeight-y) + " " + width + " " + height + " re f\n");
        }
        
        public void drawCell(float x, float y,float height, String label,int border,int align) {
        	if (border == 1) {
        		setData(x + " " + ((pageHeight-y)) + " " + ((pageWidth-pageMargin)-x) + " " + height + " re S\n");
        	} 
        	
        	if (!label.equals("")) {
        		float dx = 0;
        		if (align == Page.CENTER) {
        			dx = ((pageWidth-(label.length()*fontSize))/2)-pageMargin;
        		} else if (align == Page.RIGHT) {
        			dx = pageWidth-(label.length()*fontSize)-pageMargin-x;
        		}
        		setData("BT " + (x+dx) + " " + ((pageHeight-y)+(height/2)) + " Td (" + escapeString(label) + ") Tj ET\n");
        	}
        }
        
        public void drawBitmap(float x, float y,String filePath, float width, float height) {
        	if (!imageSet.isEmpty() && imageSet.containsKey(filePath)) {
        		XObject object = imageSet.get(filePath);
        		setData("q " + (width*scale) + " 0 0 " + (height*scale) + " " + (x*scale) + " " + ((pageHeight-(y+height))*scale) + " cm /I" + object.imageID + " Do Q");
        	} else {
        	    File file = new File(filePath);
        	    InputStream fileInput = null;
        	    try {
        		    if (file.exists()) {
        		        fileInput = new FileInputStream(file);
        		        if (fileInput != null) {
        		            int bytesRead = 0;
        		            byte []dataBuffer = new byte[fileInput.available()];
        		            fileInput.read(dataBuffer);
        		            imageSet.put(filePath,new XObject(++imageID,dataBuffer,x,y,width,height));
        		        // ((pageHeight-(y+height))*scale)
        		            setData("q " + (width*scale) + " 0 0 " + (height*scale) + " " + (x*scale) + " " + 11 + " cm /I" + imageID + " Do Q\n");
        		        }
        		    }
        	    } catch (Exception exception) {
        		    exception.printStackTrace();
        	    } finally {
        		    if (fileInput != null) {
        			    try {
        				    fileInput.close();
        			    } catch (Exception exception) {
        			    }
        			    fileInput = null;
        		    }
        	    }
        	}
        }
        
        public void setRightMargin(float value) {
            pageRightMargin = value;
        }
        
        public void setLeftMargin(float value) {
            pageLeftMargin = value;
        }
        
        public void setTopMargin(float value) {
            pageTopMargin = value;
        }
        
        public void setBottomMargin(float value) {
            pageBottomMargin = value;
        }
        
        public void setMargin(float left, float top, float right, float bottom) {
            pageLeftMargin = left;
            pageTopMargin = top;
            pageRightMargin = right;
            pageBottomMargin = bottom;
        }
        
        public float getWidth() {
            return pageWidth;
        }
        
        public float getHeight() {
            return pageHeight;
        }
        
        public int getPageNumber() {
        	return pageNumber;
        }
        
        public float getMargin() {
            return pageMargin;
        }
        
        public float getLineWidth() {
            return lineWidth;
        }
        
        public float getFontSize() {
            return fontSize;
        }
                               
        public String getContents() {
        	System.out.println(pageContent.toString());
            return pageContent.toString();
        }
        
        public void setData(String value) {
        	pageContent.append(value);
        }
        
        public void setFont(String fontFamily,String fontStyle,float fontSize) {
        	String fontKey = fontFamily.equalsIgnoreCase("Arial") ? "Helvetica" : fontFamily;
        	        	
        	if (fontStyle.equalsIgnoreCase("Bold")) {
        		fontKey = fontKey + "-Bold";
        	} else if (fontStyle.equalsIgnoreCase("Bold,Italic")) {
        		if (fontKey.equalsIgnoreCase("Times") || fontKey.equalsIgnoreCase("Times-Roman")) {
        			fontKey = "Times-BoldItalic";
        		} else {
        			fontKey = fontKey + "-BoldOblique";
        		}
        	} else if (fontStyle.equalsIgnoreCase("Italic")) {
        	    if (fontKey.equalsIgnoreCase("Times") || fontKey.equalsIgnoreCase("Times-Roman")) {
        	    	fontKey = "Times-Italic";
        	    } else {
        	        fontKey = fontKey + "-Oblique";
        	    }
        	} else {
        	    if (fontKey.equalsIgnoreCase("Times")) {
        	        fontKey = "Times-Roman";
        	    }
        	}
        	
        	int fontIdx = 1;
        	
        	if (currentFonts.isEmpty() || !currentFonts.containsKey(fontKey)) {
        	    for (String fontName:fonts) {
        	        if (fontName.equalsIgnoreCase(fontKey)) {
        	    	    this.fontFamily = fontName;
        	    	    currentFonts.put(fontKey,++fontID);
        	    	    fontIdx = fontID;
        	    	    break;
        	        }
        	    }
        	} else {
        		fontIdx = currentFonts.get(fontKey);
        	}
        	this.fontSize = fontSize;
        	
            setData("BT /F" + fontIdx + " " + fontSize + " Tf ET\n");
        }
        
        public void setDefaultFont() {
            setFont("Arial","",fontSize);
        }
        
        public void setOffset(int pageOffset) {
            this.pageOffset = pageOffset;
        }
        
        public int getOffset() {
            return pageOffset;
        }
    }
    
    public final class PaperType {
         public static final int LEGAL = 1;
         public static final int LETTER = 2;
         public static final int A4 = 3;
    }
    
    public final class XObject {
    	public byte []imageData;
    	public float imageWidth, imageHeight;
    	public float x, y;
    	public int objectID;
    	public int imageID;
    	
    	public XObject(int imageID,byte []imageData,float x, float y, float imageWidth, float imageHeight) {
    	    this.imageData = imageData;
    	    this.imageID = imageID;
    	    this.x = x;
    	    this.y = y;
    	    this.imageWidth = imageWidth;
    	    this.imageHeight = imageHeight;
    	    
    	    this.objectID = 0;
    	}
    }
}