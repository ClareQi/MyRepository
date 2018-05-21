package sample;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class FileUtils {
	
	public static void main(String[] args) {
		String path="F:\\OCR\\31号晚上测试只有灰度的数据\\原图";
		ArrayList<File> list = new ArrayList<File>();
		traverseFolder1(path,list);
	}

	public static void traverseFolder1(String path,ArrayList<File> arrayList) {
        int fileNum = 0; 
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            try {
            	for (File file2 : files) {
                    if (file2.isDirectory()) {
                        
                    } else {
                    	arrayList.add(file2);
                        fileNum++;
                    }
                }
            }catch (Exception e) {
            	try {
    				Thread.sleep(30000);
    				for (File file2 : files) {
                        if (file2.isDirectory()) {
                            
                        } else {
                        	arrayList.add(file2);
                            fileNum++;
                        }
                    }
    			} catch (Exception e1) {
    				
    			}
			}
            
        } else {
            System.out.println("文件不存在!");
            try {
				Thread.sleep(30000);
				File[] files = file.listFiles();
				if (file.exists()) {
					for (File file2 : files) {
		                if (file2.isDirectory()) {
		                    
		                } else {
		                	arrayList.add(file2);
		                    fileNum++;
		                }
		            }
				}else{
					Thread.sleep(60000);
					File[] filex = file.listFiles();
					if (file.exists()) {
						for (File file2 : filex) {
			                if (file2.isDirectory()) {
			                    
			                } else {
			                	arrayList.add(file2);
			                    fileNum++;
			                }
			            }
					}
				}
			} catch (InterruptedException e) {
				
			}
        }
        System.out.println("图片共有:" + fileNum);
    }
}
