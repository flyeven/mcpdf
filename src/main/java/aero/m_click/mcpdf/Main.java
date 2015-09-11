/**
 * Mcpdf is a drop-in replacement for PDFtk.
 *
 * It fixes PDFtk's unicode issues when filling in PDF forms,
 * and is essentially a command line interface for the iText
 * PDF library with a PDFtk compatible syntax.
 */

/*
 * Copyright (C) 2014  Volker Grabsch <grabsch@m-click.aero>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * https://www.gnu.org/licenses/agpl-3.0.html
 */

package aero.m_click.mcpdf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
 
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.AcroFields;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.XfdfReader;

public class Main
{
    public static void main(String[] args)
    {
        try {
            execute(parseArgs(args));
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("See README for more information.");
            System.exit(1);
        }
    }

    public static Config parseArgs(String[] args)
        throws FileNotFoundException
    {
        if (args.length == 0) {
            throw new RuntimeException("Missing arguments.");
        }
        Config config = new Config();
        config.pdfInputStream = new FileInputStream(args[0]);
        config.pdfOutputStream = System.out;
        config.formInputStream = null;
        config.flatten = false;
        for (int i = 1; i < args.length; i++) {
            if ("fill_form".equals(args[i])) {
                config.formInputStream = System.in;
                i++;
                if (!"-".equals(args[i])) {
                    throw new RuntimeException("Missing \"-\" after fill_form operation.");
                }
            } else if ("output".equals(args[i])) {
                i++;
                if (!"-".equals(args[i])) {
                    throw new RuntimeException("Missing \"-\" after output operation.");
                }
            } else if ("flatten".equals(args[i])) {
                config.flatten = true;
            } else {
                throw new RuntimeException("Unknown operation: " + args[i]);
            }
        }
        return config;
    }

    public static void execute(Config config)
        throws IOException, DocumentException
    {
        PdfReader reader = new PdfReader(config.pdfInputStream);
        PdfStamper stamper = new PdfStamper(reader, config.pdfOutputStream, '\0');
		
		//Font bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false); 

		//Font fontChinese = new Font(bfChinese); 

		BaseFont bf = BaseFont.createFont("C:\\WINDOWS\\Fonts\\KAIU.TTF", BaseFont.IDENTITY_H,  BaseFont.NOT_EMBEDDED);
		
		//設定中文字型(BaseFont、字型大小、字型型態)
		Font chineseFont = new Font(bf, 12, Font.NORMAL);

        if (config.formInputStream != null) {
			AcroFields form = stamper.getAcroFields();
			form.addSubstitutionFont(bf);
            form.setFields(new XfdfReader(config.formInputStream));
        }
        stamper.setFormFlattening(config.flatten);
        stamper.close();
    }
}

/**
PdfReader reader = new PdfReader(src);
PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
AcroFields form = stamper.getAcroFields();
BaseFont unicode =
    BaseFont.createFont("c:/windows/fonts/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
form.addSubstitutionFont(unicode);
form.setField("description", "\u0628\u0627 \u0628\u0627 \u0628\u0627 \u0628\u0627 \u0628\u0627 \u0628\u0627 \u0628\u0627 \u0628\u0627 \u0628\u0627 \u0628\u0627");
stamper.close();
reader.close();
*/
