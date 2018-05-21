package sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cbt.DBHelper.LocalDBHelper;

import sample.ProductAnticipationPojo;

public class ProductAnticipation{
	private String txtPath;//识别结果  txt
	
	private String logPath;
	
    public ProductAnticipation(String txtPath,String logPath) {
		this.txtPath = txtPath;
		this.logPath = logPath;
	}

	public void runProductAndShop(){
		try {
//			while(true){
//				 writeContentToTxtFile("-----------开始处理图片识别结果--------------",logPath);
				 File file = new File(txtPath);
				 algorithmFile(file);
//		         writeContentToTxtFile("-----------结束处理图片识别结果--------------",logPath);
//		         writeContentToTxtFile("-----------开始处理店铺图片识别结果--------------",logPath);
		         //将店铺id更新到产品识别结果表中product_anticipation
		         updateShopId();
		         //店铺判断
		         shopAlgorithm();
		         //针对店铺识别结果做判断，判断哪些店铺免检和必检
//		         shopAlgorithm(list);
//		         writeContentToTxtFile("-----------结束处理店铺图片识别结果--------------",logPath);
//		         Thread.sleep(1000);
//			}
		         writeContentToTxtFile("-----------文件["+txtPath+"]处理完结--------------",logPath);
		} catch (Exception e) {
			writeContentToTxtFile("-----------文件["+txtPath+"]处理错误--------------",logPath);
		}
	}

     
     /**
      * 读取txt文件的内容并判断哪些商品可以直接上架
      * @param file 想要读取的文件对象
      * @return 返回可以直接上架的商品goods_pids
      */
     public String algorithmFile(File file){
    	 String goods_pids="";
         try{
             BufferedReader br = new BufferedReader(new FileReader(file));
             String s = null;
             while((s = br.readLine())!=null){
            	 String img_info=System.lineSeparator()+s;
            	 if(img_info.indexOf("  ")>-1){
            		 String [] roots=getElement(img_info.split("  ")[0]);
            		 String goods_pid=roots[3].toString();
            		 int remark=Integer.valueOf(img_info.split("  ")[1]);
            		 if(remark==9){
            			 //产品没有详情图片
            			 insertProductState(goods_pid,remark);
            		 }else{
            			 int is_aliKey=Integer.valueOf(img_info.split("  ")[2]);
                		 //保存图片识别结果信息
                		 insertData("","",goods_pid,img_info.split("  ")[0],remark,is_aliKey);
            		 }
            	 }
             }
             br.close();  
             System.out.println("------------------图片结果存入完成----------开始处理结果");
             //当一个产品详情图片超过3张且只有一张经过OCR判断为有中文则该商品可以直接上架
             List<ProductAnticipationPojo> list=getAllImgInfo();
             goods_pids=productAlgorithm(list);
//             FileWriter fileWriter;
//			 fileWriter = new FileWriter(file);
//			 fileWriter.write("");
//	         fileWriter.flush();
//	         fileWriter.close();
         }catch(Exception e){
        	 e.printStackTrace();
        	 writeContentToTxtFile("-----------处理图片识别出错--------------",logPath);
         }
         return goods_pids;
     }
     
     
     /**
      * 获取图片路径的各个目录
      * @param imgUrl
      * @return
      */
     public  String[] getElement(String imgUrl){
    	 String [] s=new String[imgUrl.split("\\\\").length];
    	 for(int i=0;i<imgUrl.split("\\\\").length;i++){
    		 s[i]=imgUrl.split("\\\\")[i];
    	 }
    	 return s;
     }
     
     /**
      * 
      * @param ship_no  店铺名称
      * @param goods_category  产品类别
      * @param goods_pid  产品编号
      * @param img_url  图片路径
      * @param remark  是否包含中文  0没有中文  1有中文
      * @return
      */
     public int insertData(String ship_no,String goods_category,String goods_pid,String img_url,int remark,int is_aliKey){
    	 int ret=0;
    	 Connection conn = LocalDBHelper.getConnection();
    	 PreparedStatement stmt = null;
    	 ResultSet rs = null;
    	 String sql="";
    	 try{
    		 sql="select remark from product_anticipation where img_url=?";
    		 stmt=conn.prepareStatement(sql);
    		 stmt.setString(1, img_url);
    		 rs=stmt.executeQuery();
    		 if(rs.next()){
    			 int re=rs.getInt("remark");
    			 if(re==0 && remark==1){
    				 sql="update product_anticipation set remark=?,is_aliKey=? where img_url=?";
    				 stmt=conn.prepareStatement(sql);
    				 stmt.setInt(1, remark);
    				 stmt.setInt(2, is_aliKey);
    				 stmt.setString(3, img_url);
    				 stmt.executeUpdate();
    			 }
    		 }else{
    			 sql="insert into product_anticipation set ship_no=?,goods_category=?,goods_pid=?,img_url=?,remark=?,createtime=now(),is_aliKey=? ON DUPLICATE KEY UPDATE remark=?,is_aliKey=?";
        		 stmt=conn.prepareStatement(sql);
        		 stmt.setString(1, ship_no);
        		 stmt.setString(2, goods_category);
        		 stmt.setString(3, goods_pid);
        		 stmt.setString(4, img_url);
        		 stmt.setInt(5, remark);
        		 stmt.setInt(6, is_aliKey);
        		 stmt.setInt(7, remark);
        		 stmt.setInt(8, is_aliKey);
        		 ret=stmt.executeUpdate();
    		 }
    	 }catch(Exception e){
    		 writeContentToTxtFile("插入图片识别结果错误"+":"+img_url,logPath);
    	 }finally {
			try {
				stmt.close();
				rs.close();
				LocalDBHelper.returnConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
    	 return ret;
     }
     
     public List<ProductAnticipationPojo> getAllImgInfo(){
    	 Connection conn = LocalDBHelper.getConnection();
    	 PreparedStatement stmt = null;
    	 ResultSet rs = null;
    	 List<ProductAnticipationPojo> list=new ArrayList<ProductAnticipationPojo>();
    	 try{
    		 String sql="select a.goods_pid from product_anticipation a group by a.goods_pid having count(a.id)=(select count(id) from product_anticipation where goods_pid=a.goods_pid and remark=2)";
    		 stmt=conn.prepareStatement(sql);
    		 rs=stmt.executeQuery();
    		 while(rs.next()){
    			 String goods_pid=rs.getString("goods_pid");
    			 sql="update ali1688_company_offers_details set remark=1 where goods_pid='"+goods_pid+"'";
    			 stmt=conn.prepareStatement(sql);
    			 stmt.executeUpdate();
    			 insertProductState(goods_pid,2);
    		 }
    		 sql="select a.goods_pid,count(a.id) as amounts,(select count(id) from product_anticipation where goods_pid=a.goods_pid and remark=1) as conAmount,"
    		 		+ "(select count(id) from product_anticipation where goods_pid=a.goods_pid and is_aliKey=1) as aliKey "
    		 		+ "from product_anticipation a where a.flag=0 and a.remark in (0,1) group by a.goods_pid";
    		 stmt=conn.prepareStatement(sql);
    		 rs=stmt.executeQuery();
    		 while(rs.next()){
    			 ProductAnticipationPojo p=new ProductAnticipationPojo();
    			 p.setGoods_pid(rs.getString("goods_pid"));//产品ID
    			 p.setAmounts(rs.getInt("amounts"));//产品详情图片总数
    			 p.setConAmount(rs.getInt("conAmount"));//包含中文图片数量
    			 p.setAlikey(rs.getInt("aliKey"));//是否含有1688关键字
    			 list.add(p);
    		 }
    	 }catch(Exception e){
    		 writeContentToTxtFile("查询产品识别结果错误",logPath);
    	 }finally {
			try {
				stmt.close();
				rs.close();
				LocalDBHelper.returnConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
    	 return list;
     }
     
     public List<ProductAnticipationPojo> shopAlgorithm(){
    	 Connection conn = LocalDBHelper.getConnection();
    	 PreparedStatement stmt = null;
    	 ResultSet rs = null;
    	 ResultSet rs1 = null;
    	 List<ProductAnticipationPojo> list=new ArrayList<ProductAnticipationPojo>();
    	 try{
    		 String sql="select p.shop_id,count(distinct p.id) as goods_amounts,count(distinct pa.id) as key_amounts from product_state p "
    		 		+ "inner join shop_manager sp on p.shop_id=sp.shop_id "
    		 		+ "left join product_anticipation pa on sp.shop_id=pa.ship_no AND pa.is_alikey=1 "
    		 		+ "where p.status in (0,1) and sp.pro_flag=1 and sp.system_evaluation=0 group by p.shop_id";
    		 stmt=conn.prepareStatement(sql);
    		 rs1=stmt.executeQuery();
    		 while(rs1.next()){
    			 boolean flag=true;
    			 String shop_id=rs1.getString("shop_id");//需要经过判断的店铺ID
    			 int goods_amounts=rs1.getInt("goods_amounts");//该店铺下除开产品没有详情图片和全是长图的产品数量
    			 int key_amounts=rs1.getInt("key_amounts");//该店铺下产品中含有敏感词的图片数量
    			 System.out.println("店铺id【"+shop_id+"】总产品数【"+goods_amounts+"】含敏感词的图片数【"+key_amounts+"】");
    			 //必检店铺判断
    			 sql="select count(b.goods_pid) as ascounts from (select a.goods_pid from product_anticipation a where a.remark in (0,1) and a.ship_no='"+shop_id+"' group by a.goods_pid "
     				 	+ "having (select count(id) from product_anticipation where remark=1 and goods_pid=a.goods_pid)/count(a.id)*100>15) b";
				 stmt=conn.prepareStatement(sql);
	    		 rs=stmt.executeQuery();
	    		 if(rs.next()){
	    			 int ascounts=rs.getInt("ascounts");
	    			 System.out.println("店铺id【"+shop_id+"】中文占比超过15%的产品数【"+ascounts+"】,占总产品比为:【"+(Double.valueOf(ascounts)/Double.valueOf(goods_amounts)*100)+"】");
	    			 if(Double.valueOf(ascounts)/Double.valueOf(goods_amounts)*100>40){
	    				 //必检店铺
	    				 updateShopManagerState(shop_id,0);
	    				 flag=false;
	    			 }
	    		 }
    			 //免检店铺判断
    			 if(flag && goods_amounts<=8){
    				 //总量小的店铺，产品数小于等于8个,统计中文图片大于1张的产品数量
    				 sql="SELECT COUNT(b.goods_pid) ascounts FROM (select goods_pid,count(id) from product_anticipation where remark=1 and ship_no='"+shop_id+"' "
    				 		+ "and goods_pid not in (select goods_pid from product_state where shop_id='"+shop_id+"' and status in (2,9)) "
    				 		+ "group by goods_pid having count(id)>1) b";
    				 stmt=conn.prepareStatement(sql);
    	    		 rs=stmt.executeQuery();
    	    		 if(rs.next()){
    	    			 int ascounts=rs.getInt("ascounts");
    	    			 System.out.println("店铺id【"+shop_id+"】中文图片大于1张的产品数【"+ascounts+"】,占总产品比为:【"+(Double.valueOf(ascounts)/Double.valueOf(goods_amounts)*100)+"】");
    	    			 if(key_amounts==0 && Double.valueOf(ascounts)/Double.valueOf(goods_amounts)*100<=10){
    	    				 //该店铺为免检店铺
    	    				 updateShopManagerState(shop_id,1);
    	    			 }else{
    	    				 updateShopManagerState(shop_id,4);
    	    			 }
    	    		 }else{
    	    			 updateShopManagerState(shop_id,1);
    	    		 }
    			 }else if(flag){
    				 //总量大的店铺，产品数大于8个,统计该店铺下中文比例大于10%的产品数量
    				 sql="select count(b.goods_pid) as ascounts from (select a.goods_pid from product_anticipation a where a.remark in (0,1) and a.ship_no='"+shop_id+"' group by a.goods_pid "
    				 	+ "having (select count(id) from product_anticipation where remark=1 and goods_pid=a.goods_pid)/count(a.id)*100>10) b";
    				 stmt=conn.prepareStatement(sql);
    	    		 rs=stmt.executeQuery();
    	    		 if(rs.next()){
    	    			 int counts=rs.getInt("ascounts");
    	    			 System.out.println("店铺id【"+shop_id+"】中文占比超过10%的产品数【"+counts+"】,占总产品比为:【"+(Double.valueOf(counts)/Double.valueOf(goods_amounts)*100)+"】");
    	    			 if(key_amounts==0 && Double.valueOf(counts)/Double.valueOf(goods_amounts)*100<=15){
    	    				 //该店铺为免检店铺
    	    				 updateShopManagerState(shop_id,1);
    	    			 }else{
    	    				 updateShopManagerState(shop_id,4);
    	    			 }
    	    		 }else{
    	    			 updateShopManagerState(shop_id,1);
    	    		 }
    			 }
    			 //该店铺已过系统处理
    			 updateShopState(shop_id);
    		 }
    	 }catch(Exception e){
    		 e.printStackTrace();
    		 writeContentToTxtFile("店铺判断错误",logPath);
    	 }finally {
			try {
				stmt.close();
				rs.close();
				rs1.close();
				LocalDBHelper.returnConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
    	 return list;
     }
     
     /**
      * 获取哪些产品可以直接上架
      * @param list
      * @return
      */
     public  String productAlgorithm(List<ProductAnticipationPojo> list){
    	 StringBuffer bf=new StringBuffer();
    	 List<String> img_list=new ArrayList<String>();
    	 for (ProductAnticipationPojo p : list) {
    		 writeContentToTxtFile(p.getAmounts()+":开始插入产品"+":"+p.getGoods_pid()+"状态",logPath);
    		 if(p.getConAmount()==0){
    			 insertProductState(p.getGoods_pid(),1);
    		 }else if(p.getConAmount()/p.getAmounts()*100<=25 && p.getAmounts()<=8){
    			//中文图片占总详情图片25%以内,且详情图片小于等于8张,不移图片，该产品可直接上架
    			 insertProductState(p.getGoods_pid(),1);
    			//将该产品移除的图片移回来
    			img_list=getImgsByGoodsPid(p.getGoods_pid(),1);
    	  		for (String s : img_list) {
    	  			if("0".equals(s.split("@")[1].toString())){
    	  				copyFile("G:"+s.split(":")[1],s.split("@")[0].toString());
    	  			}
    	 		}
    		 }else if(p.getConAmount()/p.getAmounts()*100<=25 && p.getAmounts()>8){
    			 //中文图片占总详情图片25%以内,且详情图片大于8张，将有中文图片移除，该产品可直接上架
    			 insertProductState(p.getGoods_pid(),1);
    		 }else if(p.getConAmount()/p.getAmounts()*100<=50 && p.getAmounts()<=8){
    			 insertProductState(p.getGoods_pid(),0);
    		 }else if(p.getConAmount()/p.getAmounts()*100<=50 && p.getAmounts()>8){
    			 insertProductState(p.getGoods_pid(),1);
    		 }else if(p.getConAmount()/p.getAmounts()*100>50){
    			 insertProductState(p.getGoods_pid(),0);
    		 }else if(p.getAlikey()>2){
 				//如果一个产品的详情图片中有2张以上含有1688字样则移除该产品的所有详情图
 				img_list=getImgsByGoodsPid(p.getGoods_pid(),2);
 				for (String s : img_list) {
 					copyFile(s,"G:"+s.split(":")[1]);
 				}
 				insertProductState(p.getGoods_pid(),0);
 			}else{
 				insertProductState(p.getGoods_pid(),0);
 			}
			updateImgState(p.getGoods_pid());
		}
    	 return bf.toString().indexOf(",")>-1?bf.toString().substring(0,bf.toString().length()-1):bf.toString();
     }
     
     /**
      *记录商品状态
      * @param goods_pid
      * @return
      */
     public int insertProductState(String goods_pid,int status){
    	 writeContentToTxtFile("开始插入产品"+":"+goods_pid+"状态",logPath);
    	 int ret=0;
    	 Connection conn = LocalDBHelper.getConnection();
    	 PreparedStatement stmt = null;
    	 try{
    		 String sql="insert into product_state set goods_pid=?,status=? ON DUPLICATE KEY UPDATE status=?";
    		 stmt=conn.prepareStatement(sql);
    		 stmt.setString(1, goods_pid);
    		 stmt.setInt(2, status);
    		 stmt.setInt(3, status);
    		 ret=stmt.executeUpdate();
    		 if(status==9){
    			 sql="update ali1688_company_offers_details set remark=1  where goods_pid=?";
    			 stmt=conn.prepareStatement(sql);
    			 stmt.setString(1, goods_pid);
    			 stmt.executeUpdate();
    		 }
    		 writeContentToTxtFile("结束插入产品"+":"+goods_pid+"状态",logPath);
    	 }catch(Exception e){
    		 writeContentToTxtFile("插入产品状态标品失败",logPath);
    	 }finally {
			try {
				stmt.close();
				LocalDBHelper.returnConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
    	 return ret;
     }
     
     /**
      * 判断哪些店铺为免检哪些店铺为必检
      * @param list
      * @return
      */
//     public  String shopAlgorithm(List<ProductAnticipationPojo> list){
//    	 StringBuffer bf=new StringBuffer();
//    	 for (ProductAnticipationPojo p : list) {
//    		 System.out.println("店铺【"+p.getShip_no()+"】,可直接上架产品数【"+p.getConAmount()+"】;总产品数【"+p.getAmounts()+"】占比为【"+(Double.valueOf(p.getConAmount())/Double.valueOf(p.getAmounts())*100)+"】");
//    		 writeContentToTxtFile("店铺【"+p.getShip_no()+"】,可直接上架产品数【"+p.getConAmount()+"】;总产品数【"+p.getAmounts()+"】占比为【"+(Double.valueOf(p.getConAmount())/Double.valueOf(p.getAmounts())*100)+"】",logPath);
//			if(Double.valueOf(p.getConAmount())/Double.valueOf(p.getAmounts())*100>=80){
//				//当可直接上架产品占总产品80%以上，则该店铺免检
//				updateShopManagerState(p.getShip_no(),1);
//			}else if(Double.valueOf(p.getConAmount())/Double.valueOf(p.getAmounts())*100<=40){
//				//当可直接上架产品占总产品40%以内，则该店铺免检
//				updateShopManagerState(p.getShip_no(),0);
//			}else{
//				updateShopManagerState(p.getShip_no(),4);
//			}
//			updateShopState(p.getShip_no());
//		}
//    	 return bf.toString();
//     }
     
     /**
      * 根据goods_pid获取需要移动的图片路径
      * @param goods_pid
      * @return
      */
     public  List<String> getImgsByGoodsPid(String goods_pid,int type){
    	 List<String> list=new ArrayList<String>();
    	 Connection conn = LocalDBHelper.getConnection();
    	 PreparedStatement stmt = null;
    	 ResultSet rs = null;
    	 String sql="";
    	 try{
    		 if(type==1){
    			 sql="select img_url,is_aliKey from product_anticipation where goods_pid=? and remark=1";
    		 }else if(type==2){
    			 sql="select img_url,is_aliKey from product_anticipation where goods_pid=? and remark=0";
    		 }
    		 stmt=conn.prepareStatement(sql);
    		 stmt.setString(1, goods_pid);
    		 rs=stmt.executeQuery();
    		 while(rs.next()){
    			 list.add(rs.getString("img_url")+"@"+rs.getString("is_aliKey"));
    		 }
    	 }catch(Exception e){
    		 writeContentToTxtFile("查询需要移除图片出错",logPath);
    	 }finally {
			try {
				stmt.close();
				LocalDBHelper.returnConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
    	 return list;
     }
     
     /**
      * 标记店铺是否免检和必检  1免检  0禁用
      * @param goods_pid
      * @return
      */
     public  int updateShopManagerState(String shop_id,int state){
    	 int ret=0;
    	 Connection conn = LocalDBHelper.getConnection();
    	 PreparedStatement stmt = null;
    	 try{
    		 String sql="update shop_manager set remark="+state+" where shop_id=?";
    		 stmt=conn.prepareStatement(sql);
    		 stmt.setString(1, shop_id);
    		 ret=stmt.executeUpdate();
    	 }catch(Exception e){
    		  writeContentToTxtFile("更新店铺状态错误"+":"+shop_id+":"+state,logPath);
    	 }finally {
			try {
				stmt.close();
				LocalDBHelper.returnConnection(conn);
			} catch (SQLException e) {
				System.out.println("关闭数据库链接错误");
			}
			
		}
    	 return ret;
     }
     
     /**
      * 标记该产品已经经过优化处理
      * @param goods_pid
      * @return
      */
     public  int updateImgState(String goods_pid){
    	 int ret=0;
    	 Connection conn = LocalDBHelper.getConnection();
    	 PreparedStatement stmt = null;
    	 try{
    		 String sql="update product_anticipation set flag=1 where goods_pid=?";
    		 stmt=conn.prepareStatement(sql);
    		 stmt.setString(1, goods_pid);
    		 ret=stmt.executeUpdate();
    		 sql="update ali1688_company_offers_details set remark=1  where goods_pid=?";
			 stmt=conn.prepareStatement(sql);
			 stmt.setString(1, goods_pid);
			 stmt.executeUpdate();
    	 }catch(Exception e){
    		 writeContentToTxtFile("更新产品状态错误"+":"+goods_pid,logPath);
    	 }finally {
			try {
				stmt.close();
				LocalDBHelper.returnConnection(conn);
			} catch (SQLException e) {
				System.out.println("关闭数据库链接错误");
			}
			
		}
    	 return ret;
     }
     
     /**
      * 标记店铺已经经过判断
      * @return
      */
     public  int updateShopState(String shop_id){
    	 int ret=0;
    	 Connection conn = LocalDBHelper.getConnection();
    	 PreparedStatement stmt = null;
    	 try{
    		 String sql="update shop_manager set system_evaluation=1 where shop_id=?";
    		 stmt=conn.prepareStatement(sql);
    		 stmt.setString(1, shop_id);
    		 ret=stmt.executeUpdate();
    	 }catch(Exception e){
    		 writeContentToTxtFile("更新店铺已处理失败"+":"+shop_id,logPath);
    	 }finally {
			try {
				stmt.close();
				LocalDBHelper.returnConnection(conn);
			} catch (SQLException e) {
				System.out.println("关闭数据库链接错误");
			}
			
		}
    	 return ret;
     }
     
     /**
      * 将店铺id更新到产品识别结果表中product_anticipation
      * @return
      */
     public  int updateShopId(){
    	 int ret=0;
    	 Connection conn = LocalDBHelper.getConnection();
    	 PreparedStatement stmt = null;
    	 ResultSet rs = null;
    	 try{
    		 String sql="UPDATE product_anticipation AS a LEFT JOIN ali1688_company_offers_details AS b ON a.goods_pid=b.goods_pid SET a.ship_no=b.shop_id WHERE a.goods_pid=b.goods_pid  and a.shop_flag=0";
    		 stmt=conn.prepareStatement(sql);
    		 ret=stmt.executeUpdate();
    		 sql="UPDATE product_state AS a LEFT JOIN ali1688_company_offers_details AS b ON a.goods_pid=b.goods_pid SET a.shop_id=b.shop_id WHERE a.goods_pid=b.goods_pid  and a.shop_id is null";
    		 stmt=conn.prepareStatement(sql);
    		 ret=stmt.executeUpdate();
    		 sql="select a.shop_id from ali1688_company_offers_details a group by shop_id having count(a.id)-(select count(id) from ali1688_company_offers_details where shop_id=a.shop_id and remark=1)=0";
    		 stmt=conn.prepareStatement(sql);
    		 rs=stmt.executeQuery();
    		 while(rs.next()){
    			 sql="update shop_manager set pro_flag=1 where shop_id =?";
    			 stmt=conn.prepareStatement(sql);
    			 stmt.setString(1, rs.getString("shop_id"));
    			 stmt.executeUpdate();
    		 }
    	 }catch(Exception e){
    		 writeContentToTxtFile("更新产品店铺失败",logPath);
    	 }finally {
			try {
				stmt.close();
				rs.close();
				LocalDBHelper.returnConnection(conn);
			} catch (SQLException e) {
				System.out.println("关闭数据库链接错误");
			}
			
		}
    	 return ret;
     }
     
     
     
     /**
      * 将图片从src路径移到target
      * @param src
      * @param target
      */
     public  void copyFile(String src,String target) {  
    	 Pattern p = Pattern.compile("\\s*|\t|\r|\n");
    	 Matcher m = p.matcher(target);
    	 target = m.replaceAll("");
    	 System.out.println("原路径："+src);
    	 System.out.println("新路径路径："+target);
    	 File srcFile = new File(src);    
         File targetFile = new File(target);
         try {
        	 File file = new File(target.substring(0,target.lastIndexOf("\\")));
        	 if (!file.exists()) {
        		 file.mkdir();
        	 }
             InputStream in = new FileInputStream(srcFile);     
             OutputStream out = new FileOutputStream(targetFile);    
             byte[] bytes = new byte[1024];    
             int len = -1;    
             while((len=in.read(bytes))!=-1)  
             {    
                 out.write(bytes, 0, len);    
             }    
             in.close();    
             out.close();    
         } catch (FileNotFoundException e) {    
            writeContentToTxtFile("文件复制失败从"+":"+src+"至"+target,logPath);
         } catch (IOException e) {    
        	 writeContentToTxtFile("文件复制失败从"+":"+src+"至"+target,logPath);
         }    
         writeContentToTxtFile("文件复制成功从"+":"+src+"至"+target,logPath);
     } 
     
     /**
      * 将内容写入到txt文件中
      * content:内容
      * writePath:txt文件路径
      * */
     public  void writeContentToTxtFile(String content,String writePath){
 		 try {
 			FileWriter fw0 = new FileWriter(writePath,true);
 			BufferedWriter bw0 = new BufferedWriter(fw0);
 			bw0.write(content+"\r\n");
 			bw0.close();
 			fw0.close();
 		} catch (IOException e) {
 			System.out.println("ss");
 		}
 	}
     
//   public static void main(String[] args) {
//	 String pic_disk="E:\\ocr_txtDir\\";//
//	 File file=new File(pic_disk);
//	 File flist[] = file.listFiles();
//	  if (flist == null || flist.length == 0) {
////		  writeContentToTxtFile("没有文件","E:/log.txt");
//	  }
//	  for (File f : flist) {
//	      if (!f.isDirectory()) {
//	          System.out.println("file==>" + f.getAbsolutePath());
//	          ProductAnticipation p=new ProductAnticipation(f.getAbsolutePath(),"E:/log.txt");
//	         p.readFile();
//	      }
//	  }
//}
}
