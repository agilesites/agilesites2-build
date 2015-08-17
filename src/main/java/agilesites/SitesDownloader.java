package agilesites;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Bharath on 6/15/2015.
 * <p/>
 *
 * Usage:
 *
 * new SitesDownloader("user", "pass").download("folder", "file.zip");
 * }
 */
public class SitesDownloader {

    private String username;
    private String password;
    private CookieStore cookieStore;

    private final String sso_params_url = "https://updates.oracle.com/Orion/Services/download";
    private final String sites_url = "http://download.oracle.com/otn/nt/middleware/11g/111180/ofm_sites_generic_11.1.1.8.0_disk1_1of1.zip";
    private final String sites_zip_file_name = "WCS_Sites.zip";
    private String downloaded_file_path = "";
    private String downloaded_file_name = "owcs_11.1.1.8.zip";
    private static int percentage = 0;


    public SitesDownloader(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getSsoLocation() {

        String location = "";
        HttpResponse response = call(sso_params_url);
        if (response != null) {


            location = response.getFirstHeader("Location").getValue();
            String SSO_TOKEN = location.split("=")[1];
            String SSO_SERVER = location.substring(0, location.indexOf("p", location.indexOf("p") + 1));

            try {
                //creating post url request
                String SSO_AUTH_URL = "sso/auth";
                HttpPost sso_request = new HttpPost(SSO_SERVER.concat(SSO_AUTH_URL));
                System.out.println("Checking credentials... ");

                //setting parametrs
                ArrayList<NameValuePair> postParameters;
                postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("ssousername", username));
                postParameters.add(new BasicNameValuePair("password", password));
                postParameters.add(new BasicNameValuePair("site2pstoretoken", SSO_TOKEN));
                sso_request.setEntity(new UrlEncodedFormEntity(postParameters));

                //cookie store
                BasicCookieStore cookieStore = new BasicCookieStore();
                HttpContext localContext = new BasicHttpContext();
                localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(builder.build());


                CookieHandler.setDefault(new CookieManager());
                HttpClient httpclient = HttpClientBuilder.create().setUserAgent("Mozilla/5.0").setSSLSocketFactory(sf).setRedirectStrategy(new LaxRedirectStrategy()).build();
                HttpResponse sso_response = httpclient.execute(sso_request, localContext);
                if (sso_response.getStatusLine().getStatusCode() < 200 || sso_response.getStatusLine().getStatusCode() >= 400) {
                    throw new IOException("Got bad response, error code = " + sso_response.getStatusLine().getStatusCode());
                } else {

                    System.out.println("Reading sso properties...");
                    this.cookieStore = (BasicCookieStore) localContext.getAttribute(HttpClientContext.COOKIE_STORE);
                    List<Cookie> sso_cookies = cookieStore.getCookies();
                    BasicClientCookie accept_cookie = new BasicClientCookie("oraclelicense", "accept-securebackup-cookie");
                    accept_cookie.setPath("/");
                    accept_cookie.setDomain(".oracle.com");
                    accept_cookie.setVersion(0);

                    this.cookieStore.addCookie(accept_cookie);
                    for (Object c : sso_cookies) {
                        Cookie cookie = (Cookie) c;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }

        }
        return location;
    }

    public HttpResponse call(String url) {
        try {
            HttpGet request = new HttpGet(url);
            HttpParams params = request.getParams();
            params.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
            request.setParams(params);

            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

            HttpClient httpclient = HttpClientBuilder.create().setUserAgent("Mozilla/5.0").setSSLSocketFactory(sslsf).build();
            HttpResponse response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 400) {
                throw new IOException("Got bad response, error code = " + response.getStatusLine().getStatusCode());
            } else {
                return response;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void download(String download_directory, String download_filename) {
        File target_directory = new File(download_directory);
        if (download_directory != null && download_directory.length() > 0 && !target_directory.exists()) {
            target_directory.mkdir();
            System.out.println("Created the '" + target_directory + "' folder.");

        }
        if (download_filename != null && download_filename.length() > 3 && (download_filename.endsWith(".zip") || download_filename.endsWith(".ZIP"))) {
            downloaded_file_name = download_filename;
        } else {
            System.out.println("filename is not valid. It should end with '.zip' extension. Please check and try again.");
        }

        getSsoLocation();

        if (this.cookieStore.getCookies().size() <= 3) {
            System.out.println("Authentication is failed. Please check your credentials.");
        } else {
            downloaded_file_path = download_directory;
            try {

                System.out.println("Connecting " + sites_url);

                HttpGet httpget = new HttpGet(sites_url);

                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(builder.build());

                HttpContext localContext = new BasicHttpContext();
                localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

                HttpClient httpclient = HttpClientBuilder.create().setUserAgent("Mozilla/5.0").setSSLSocketFactory(sf).setRedirectStrategy(new LaxRedirectStrategy()).build();

                httpget.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

                HttpResponse response = httpclient.execute(httpget, localContext);

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String content_type = entity.getContentType().toString();
                    InputStream instream = entity.getContent();

                    if (content_type.contains("text/html")) {
                        //authentication failed
                        System.out.println("Authentication failed");

                    } else {
                        //downloading zip file
                        System.out.println("Downloading file of size =" + entity.getContentLength() + ", type is " + entity.getContentType());


                        try {
                            BufferedInputStream bis = new BufferedInputStream(instream);
                            String filePath = downloaded_file_path + "/" + downloaded_file_name.substring(0, downloaded_file_name.lastIndexOf("."));
                            File download_file = new File(filePath);
                            if (download_file.exists()) {
                                download_file.delete();
                                System.out.println("File is already existed in the path(" + filePath + "). Deleted the existed file and create a fresh file.");
                            }
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(download_file));
                            byte data[] = new byte[1024];
                            int count;
                            long total = 0;
                            long lenghtOfFile = entity.getContentLength();

                            while ((count = bis.read(data)) != -1) {
                                total += count;
                                bos.write(data, 0, count);
                                showProgress(total, lenghtOfFile);
                            }

                            bis.close();
                            bos.close();
                            download_file.renameTo(new File(filePath.concat(".zip")));

                            //unzip the files
                            look_OWCS_Sites();
                        } catch (IOException ex) {
                            throw ex;
                        } catch (RuntimeException ex) {
                            httpget.abort();
                            throw ex;
                        } finally {
                            instream.close();
                        }

                    }

                    httpclient.getConnectionManager().shutdown();
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

    static long nextProgress = System.currentTimeMillis() + 5000;

    private static void showProgress(long total, long lenghtOfFile) {
        long now = System.currentTimeMillis();
        if (now < nextProgress) return;
        //long percentage = (total*100.0)/lenghtOfFile;
        System.out.println(new DecimalFormat("#.##").format((total * 100.0) / lenghtOfFile) + "% (" + total + " of " + lenghtOfFile + ")");
        nextProgress = now + 5000;
    }

    public void look_OWCS_Sites() {
        //look for WCS_Sites.zip file in downloaded zip file
        String unzipped_directory = unzip(downloaded_file_path + File.separator + downloaded_file_name, downloaded_file_path,sites_zip_file_name);
        if (unzipped_directory.length() > 0) {
            //String owcs_dest = unzipped_directory + File.separator + "WCS_Sites";
            unzip(unzipped_directory + File.separator + sites_zip_file_name, downloaded_file_path,null);

        }
    }

    public String unzip(String filepath, String destinaton_directory, String lookUpFile) {
        File destDir = new File(destinaton_directory);
        String root_directory = "";

        if (!destDir.exists()) {
            destDir.mkdir();
        }

        try {
            //System.out.println("unzipping.");
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(filepath));
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                String filePath = destinaton_directory + File.separator + entry.getName();

                if( lookUpFile != null && lookUpFile.length() > 0 ){

                    if( (!entry.isDirectory()) && entry.getName().contains(lookUpFile) ){
                        //found the lookUpFile
                        System.out.println("Found "+entry.getName());
                        filePath = destinaton_directory + File.separator + lookUpFile;
                        extractFile(zipIn, filePath);
                        System.out.println("extracting lookUpFile = " + lookUpFile + "...");
                        root_directory = destinaton_directory;
                        break;
                    }
                    /*skip the iteration*/
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                    continue;
                }
                //System.out.println("2..");
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                    System.out.println("extracting " + filePath + "...");
                } else {
                    // if the entry is a directory, make the directory

                    File dir = new File(filePath);
                    dir.mkdir();
                    if (root_directory.length() <= 0) {
                        root_directory = dir.getName();
                    }

                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();

            }
            zipIn.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //looking for WCS_Sites folder
        }
        return root_directory;
    }

    public String unzipSites(String filepath, String destinaton_directory) {
        File destDir = new File(destinaton_directory);
        String root_directory = "";

        if (!destDir.exists()) {
            destDir.mkdir();
        }

        try {
            //System.out.println("unzipping.");
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(filepath));
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {

                if( entry.getName().contains("WCS_Sites.zip")){
                    //String filePath = destinaton_directory + File.separator + entry.getName();
                    String filePath = destinaton_directory + File.separator + "WCS_Sites.zip";
                    System.out.println("extracting "+entry.getName()+" - "+filePath);
                    if (!entry.isDirectory()) {
                        // if the entry is a file, extracts it
                        extractFile(zipIn, filePath);
                        System.out.println("extracting " + filePath + "...");
                    } else {
                        // if the entry is a directory, make the directory

                        File dir = new File(filePath);
                        dir.mkdir();
                        if (root_directory.length() <= 0) {
                            root_directory = dir.getName();
                        }

                    }
                }


                zipIn.closeEntry();
                entry = zipIn.getNextEntry();

            }
            zipIn.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //looking for WCS_Sites folder
        }
        return root_directory;

    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        final int BUFFER_SIZE = 4096;
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
