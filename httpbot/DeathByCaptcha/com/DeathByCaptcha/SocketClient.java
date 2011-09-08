package com.DeathByCaptcha;

import org.base64.Base64;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;


/**
 * Death by Captcha socket API client.
 *
 * @author Sergey Kolchin <ksa242@gmail.com>
 */
public class SocketClient extends Client
{
    final static public String HOST = "api.deathbycaptcha.com";
    final static public int FIRST_PORT = 8123;
    final static public int LAST_PORT = 8130;


    protected SocketChannel channel = null;
    protected Object callLock = new Object();


    private class SocketClientCaller
    {
        public String call(SocketChannel channel, byte[] payload, Date deadline)
            throws IOException
        {
            ByteBuffer sbuf = ByteBuffer.wrap(payload);
            ByteBuffer rbuf = ByteBuffer.allocateDirect(256);
            CharsetDecoder rbufDecoder = Charset.forName("UTF-8").newDecoder();

            int ops = SelectionKey.OP_WRITE | SelectionKey.OP_READ;
            if (channel.isConnectionPending()) {
                ops = ops | SelectionKey.OP_CONNECT;
            }
            Selector selector = Selector.open();
            try {
                channel.register(selector, ops);
                StringBuilder response = new StringBuilder();
                while (deadline.after(new Date())) {
                    if (0 < selector.select(Client.POLLS_INTERVAL * 1000)) {
                        Iterator keys = selector.selectedKeys().iterator();
                        while (keys.hasNext()) {
                            SelectionKey key = (SelectionKey)keys.next();
                            SocketChannel ch = (SocketChannel)key.channel();
                            if (key.isConnectable()) {
                                // Just connected
                                ch.finishConnect();
                            } else if (key.isWritable() && sbuf.hasRemaining()) {
                                // Sending the request
                                while (0 < ch.write(sbuf) && sbuf.hasRemaining()) {
                                    //
                                }
                            } else if (key.isReadable() && !sbuf.hasRemaining()) {
                                // Receiving the response
                                while (0 < ch.read(rbuf)) {
                                    rbuf.flip();
                                    response.append(rbufDecoder.decode(rbuf).toString());
                                }
                            }
                            keys.remove();
                        }
                    }
                    if (0 < response.length() && '\n' == response.charAt(response.length() - 1)) {
                        response.setLength(response.length() - 1);
                        return response.toString();
                    }
                }
            } catch (java.lang.Exception e) {
                throw new IOException("API communication failed: " + e.toString());
            } finally {
                selector.close();
            }
            return null;
        }
    }


    /**
     * @see com.DeathByCaptcha.Client#close
     */
    public void close()
    {
        if (null != this.channel) {
            this.log("CLOSE");

            if (this.channel.isConnected() || this.channel.isConnectionPending()) {
                try {
                    this.channel.socket().shutdownOutput();
                    this.channel.socket().shutdownInput();
                } catch (java.lang.Exception e) {
                    //
                } finally {
                    try {
                        this.channel.close();
                    } catch (java.lang.Exception e) {
                        //
                    }
                }
            }

            try {
                this.channel.socket().close();
            } catch (java.lang.Exception e) {
                //
            }

            this.channel = null;
        }
    }

    /**
     * @see com.DeathByCaptcha.Client#connect
     */
    public boolean connect()
        throws IOException
    {
        if (null == this.channel) {
            this.log("OPEN");

            InetAddress host = null;
            try {
                host = InetAddress.getByName(SocketClient.HOST);
            } catch (java.lang.Exception e) {
                //System.out.println(e)
                throw new IOException("API host not found");
            }

            this.channel = SocketChannel.open();
            this.channel.configureBlocking(false);
            try {
                this.channel.connect(new InetSocketAddress(
                    host,
                    SocketClient.FIRST_PORT + new Random().nextInt(
                        SocketClient.LAST_PORT - SocketClient.FIRST_PORT + 1
                    )
                ));
            } catch (IOException e) {
                this.close();
                throw new IOException("API connection failed");
            }
        }

        return null != this.channel;
    }


    protected JSONObject call(String cmd, byte[] data, Date deadline)
        throws IOException, com.DeathByCaptcha.Exception
    {
        this.log("SEND", new String(data, 0, data.length));
        JSONObject response = null;
        while (deadline.after(new Date()) && null == response) {
            synchronized (this.callLock) {
                if (this.connect()) {
                    try {
                        response =
                            new JSONObject((new SocketClientCaller()).call(
                                this.channel,
                                data,
                                deadline
                            ));
                    } catch (java.lang.Exception e) {
                        //System.out.println("SocketClient.call(): " + e.toString());
                        this.close();
                    }
                }
            }
        }
        try {
            if (null == response) {
                throw new IOException("API connection lost or timed out");
            } else {
                this.log("RECV", response.toString());
                int status = response.optInt("status", 0xff);
                if (0x00 < status && 0x10 > status) {
                    throw new AccessDeniedException("Access denied, please check your credentials and/or balance");
                } else if (0x10 <= status && 0x20 > status) {
                    throw new InvalidCaptchaException("CAPTCHA was rejected by the service, check if it's a valid image");
                } else if (0xff == status) {
                    throw new IOException("API server error occured");
                }
            }
        } catch (IOException e) {
            //System.out.println("SocketClient.call(): " + e.toString());
            synchronized (this.callLock) {
                this.close();
            }
            throw e;
        }
        return response;
    }

    protected JSONObject call(String cmd, byte[] data)
        throws IOException, com.DeathByCaptcha.Exception
    {
        return this.call(cmd, data,
                         new Date(System.currentTimeMillis() + Client.DEFAULT_TIMEOUT * 1000));
    }

    protected JSONObject call(String cmd, JSONObject args)
        throws IOException, com.DeathByCaptcha.Exception
    {
        try {
            args.put("cmd", cmd).put("version", Client.API_VERSION);
        } catch (JSONException e) {
            //System.out.println(e);
            return new JSONObject();
        }
        return this.call(cmd, (args.toString() + "\n").getBytes());
    }


    /**
     * @see com.DeathByCaptcha.Client#Client(String, String)
     */
    public SocketClient(String username, String password)
    {
        super(username, password);
    }

    public void finalize()
    {
        this.close();
    }


    /**
     * @see com.DeathByCaptcha.Client#getUser
     */
    public User getUser()
        throws IOException, com.DeathByCaptcha.Exception
    {
        return new User(this.call("user", this.getCredentials()));
    }

    /**
     * @see com.DeathByCaptcha.Client#upload
     */
    public Captcha upload(byte[] img)
        throws IOException, com.DeathByCaptcha.Exception
    {
        JSONObject args = this.getCredentials();
        try {
            args.put("captcha",
                     Base64.encodeBytes(img)).put("swid",
                                                  Client.SOFTWARE_VENDOR_ID);
        } catch (JSONException e) {
            //System.out.println(e);
        }
        Captcha c = new Captcha(this.call("upload", args));
        return c.isUploaded() ? c : null;
    }

    /**
     * @see com.DeathByCaptcha.Client#getCaptcha
     */
    public Captcha getCaptcha(int id)
        throws IOException, com.DeathByCaptcha.Exception
    {
        JSONObject args = this.getCredentials();
        try {
            args.put("captcha", id);
        } catch (JSONException e) {
            //System.out.println(e);
        }
        return new Captcha(this.call("captcha", args));
    }

    /**
     * @see com.DeathByCaptcha.Client#remove
     */
    public boolean remove(int id)
        throws IOException, com.DeathByCaptcha.Exception
    {
        JSONObject args = this.getCredentials();
        try {
            args.put("captcha", id);
        } catch (JSONException e) {
            //System.out.println(e);
        }
        return !(new Captcha(this.call("remove", args))).isUploaded();
    }

    /**
     * @see com.DeathByCaptcha.Client#report
     */
    public boolean report(int id)
        throws IOException, com.DeathByCaptcha.Exception
    {
        JSONObject args = this.getCredentials();
        try {
            args.put("captcha", id);
        } catch (JSONException e) {
            //System.out.println(e);
        }
        return !(new Captcha(this.call("report", args))).isCorrect();
    }
}
