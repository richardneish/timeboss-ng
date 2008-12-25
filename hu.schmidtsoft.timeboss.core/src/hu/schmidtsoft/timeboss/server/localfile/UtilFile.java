package hu.schmidtsoft.timeboss.server.localfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import org.dom4j.DocumentException;

public class UtilFile {

	public static void copy(File src, File trg) throws IOException {
		InputStream in = new FileInputStream(src);
		try {
			OutputStream out = new FileOutputStream(trg);
			try {
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} finally {
				out.close();

			}
		} finally {
			in.close();
		}
	}
	public static void fixOrig(File orig, File trg) throws IOException, DocumentException
	{
		if(!orig.exists())
		{
			UtilFile.copy(trg, orig);
		}
	}
	public static Manifest loadManifest(File f) throws IOException
	{
		FileInputStream fis=new FileInputStream(f);
		try
		{
			Manifest mf=new Manifest(fis);
			return mf;
		}finally
		{
			fis.close();
		}

	}
	public static void saveManifest(File f, Manifest mf) throws IOException
	{
		FileOutputStream fos=new FileOutputStream(f);
		try
		{
			mf.write(fos);
		}finally
		{
			fos.close();
		}

	}
	public static File getWorkspace(String[] args) {
		if(args.length>0)
		{
			return new File(args[0]);
		}else
		{
			return new File("../");
		}
	}
	public static List<File> listFiles(File file) {
		File[] fs=file.listFiles();
		List<File> ret=new ArrayList<File>();
		if(fs!=null)
		{
			for(File f:fs)
			{
				ret.add(f);
			}
		}
		return ret;
	}
	public static String loadAsString(InputStream fis) throws IOException {
		try
		{
			InputStreamReader reader=new InputStreamReader(fis, "UTF-8");
			StringBuilder ret=new StringBuilder();
			int ch;
			while((ch=reader.read())>=0)
			{
				ret.append((char)ch);
			}
			return ret.toString();
		}finally
		{
			fis.close();
		}
	}

	public static String loadAsString(File f) throws IOException {
		FileInputStream fis=new FileInputStream(f);
		try
		{
			InputStreamReader reader=new InputStreamReader(fis, "UTF-8");
			StringBuilder ret=new StringBuilder();
			int ch;
			while((ch=reader.read())>=0)
			{
				ret.append((char)ch);
			}
			return ret.toString();
		}finally
		{
			fis.close();
		}
	}
	public static byte[] loadFile(File f) throws IOException {
		FileInputStream fis=new FileInputStream(f);
		try
		{
			ByteArrayOutputStream ret=new ByteArrayOutputStream();
			int ch;
			while((ch=fis.read())>=0)
			{
				ret.write(ch);
			}
			return ret.toByteArray();
		}finally
		{
			fis.close();
		}
	}
	public static void saveFile(File f, String newContent) throws IOException {
		FileOutputStream fos=new FileOutputStream(f);
		try
		{
			OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
			osw.write(newContent);
			osw.close();
		}finally
		{
			fos.close();
		}
	}
	public static void saveFile(File f, byte[] prefs) throws IOException {
		FileOutputStream fos=new FileOutputStream(f);
		try
		{
			fos.write(prefs);
		}finally
		{
			fos.close();
		}
	}
}
