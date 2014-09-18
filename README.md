SimplePDF
=========

Simple PDF Document Generator


Example:

 SimplePDF pdf = new SimplePDF();
 
 Page page = pdf.createPage(PaperType.LETTER);
 page.setFont(32.0f);
 page.setTextColor(3,3,3);
 page.setText(0,0,"This is a test");
 
 page.setDrawColor(255,0,0);
 page.drawLine(10,page.getMargin()+10,page.getWidth()-page.getMargin());

 pdf.writeTo(getOutputStream());
