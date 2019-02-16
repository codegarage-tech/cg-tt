package tech.codegarage.tidetwist.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class XmlManager {

    private static final String TAG = XmlManager.class.getSimpleName();

    public static org.w3c.dom.Document loadXmlFromAsset(Context context, String fileFullPath) {
        org.w3c.dom.Document mDoc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileFullPath);
            mDoc = db.parse(inputStream);
            mDoc.getDocumentElement().normalize();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }
        return mDoc;
    }

    public static org.w3c.dom.Document loadXmlFromServer(String xmlResponse) {
        org.w3c.dom.Document mDoc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes());
            mDoc = db.parse(inputStream);
            mDoc.getDocumentElement().normalize();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }
        return mDoc;
    }

    public static org.w3c.dom.Document loadXmlFromSDCard(String fileFullPath) {
        org.w3c.dom.Document mDoc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File dataFile = new File(fileFullPath);
            if (!dataFile.exists()) {
                return null;
            }
            InputStream inputStream = new FileInputStream(dataFile.getAbsolutePath());
            mDoc = db.parse(inputStream);
            mDoc.getDocumentElement().normalize();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }
        return mDoc;
    }

    public static NodeList getElementsByTagName(org.w3c.dom.Document document, String tagName) {
        if (document != null) {
            return document.getElementsByTagName(tagName);
        }
        return null;
    }

    private static String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public static String getValue(Element item, String value) {
        NodeList n = item.getElementsByTagName(value);
        return getElementValue(n.item(0));
    }
}