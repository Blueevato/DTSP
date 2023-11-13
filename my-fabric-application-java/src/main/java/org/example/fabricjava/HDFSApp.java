//package org.example.fabricjava;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FSDataOutputStream;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.IOUtils;
//import org.apache.hadoop.security.UserGroupInformation;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.PrivilegedExceptionAction;
//
///**
// * 实现java远程操作hdfs：远程创建目录、创建文件、上传文件、下载文件、读取文件、重命名、删除文件
// */
//public class HDFSApp {
//    static FileSystem hdfs;
//
//    //初始化访问hdfs的配置信息
//    static {
//        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("root");
//        try {
//            ugi.doAs(new PrivilegedExceptionAction<Void>() {
//                public Void run() throws Exception {
//                    Configuration conf = new Configuration();
//                    conf.set("dfs.client.use.datanode.hostname", "true");
//                    conf.set("fs.defaultFS", "hdfs://127.0.0.1:9000/");
//                    conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
//                    Path path = new Path("hdfs://127.0.0.1:9000/");
//                    hdfs = FileSystem.get(path.toUri(), conf);
//                    //hdfs = path.getFileSystem(conf); // 这个也可以
//                    return null;
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 方法2：创建文件
//     *
//     * @throws IOException
//     */
//    public static void createFile() throws IOException {
//        String fileName = "/test/myfile.txt";
//        String fileContent = "this is new file";
//        Path dst = new Path(fileName);
//        if (hdfs.exists(dst)) {
//            System.out.println("Error:文件已存在");
//        } else {
//            //将文件内容转成字节数组
//            byte[] bytes = fileContent.getBytes();
//            FSDataOutputStream output = hdfs.create(dst);
//            output.write(bytes);
//            output.close();
//            System.out.println("创建文件\t" + fileName);
//        }
//    }
//
//    /**
//     * 方法3：读取HDFS文件，并在本地控制台打印
//     *
//     * @throws IOException
//     */
//    public static void readFile() throws IOException {
//        String uri = "/test/myfile.txt";
//        // 判断文件是否存在
//        if (!hdfs.exists(new Path(uri))) {
//            System.out.println("Error;文件不存在");
//            return;
//        }
//        InputStream in = null;
//        try {
//            in = hdfs.open(new Path(uri));
//            // 复制到标准输出流
//            IOUtils.copyBytes(in, System.out, 4096, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeStream(in);
//        }
//    }
//
//    /**
//     * 方法6：重命名hdfs上面的文件
//     *
//     * @throws IOException
//     */
//    public static void renameFile() throws IOException {
//        String oldName = "/test/myfile.txt";
//        String newName = "/test/myfile1.txt";
//        Path oldPath = new Path(oldName);
//        Path newPath = new Path(newName);
//        if (hdfs.exists(oldPath)) {
//            hdfs.rename(oldPath, newPath);
//            System.out.println("rename success");
//        } else {
//            System.out.println("文件不存在,rename fail");
//        }
//    }
//
//    /**
//     * 方法7：给hdfs上面的文件追加内容
//     *
//     * @throws IOException
//     */
//    public static void appendFile() throws IOException {
//        String fileName = "/test/myfile1.txt";
//        String appendContent = "这是追加的内容";
//        Path dst = new Path(fileName);
//        byte[] bytes = appendContent.getBytes();
//        //如果文件不存在
//        if (!hdfs.exists(dst)) {
//            System.out.println("Error:文件不存在");
//            return;
//        }
//        FSDataOutputStream output = hdfs.append(dst);
//        output.write(bytes);
//        output.close();
//        System.out.println("success:追加内容到\t" + fileName);
//    }
//
//    /**
//     * 方法8：删除hdfs上面的文件
//     *
//     * @param fileName
//     * @throws IOException
//     */
//    public static void deleteFile(String fileName) throws IOException {
//        if ("".equals(fileName)) {
//            fileName = "/test/myfile1.txt";
//        }
//        Path f = new Path(fileName);
//        boolean isExists = hdfs.exists(f);
//        if (isExists) {
//            boolean isDel = hdfs.delete(f, true);
//            System.out.println(fileName + "删除状态：" + isDel);
//        } else {
//            System.out.println(fileName + "文件不存在！");
//        }
//    }
//
//    public static void main(String[] args) throws IOException {
//        System.out.println("Hello World!");
//        createFile();
//        readFile();
////        deleteFile("/test/myfile.txt");
//    }
//
//}
