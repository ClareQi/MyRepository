package sample;
import java.util.ArrayList;

public class mainMethod {
	public static void main(String[] args) {
		ArrayList<String> dirList = new ArrayList<String>();
//		dirList.add("Y:\\historyimgdesc\\historyimgdesc66");
//		dirList.add("Y:\\historyimgdesc\\historyimgdesc67");
//		dirList.add("Y:\\historyimgdesc\\historyimgdesc68");
//		dirList.add("Y:\\historyimgdesc\\historyimgdesc69");
		dirList.add("Q:\\commoncoreimg\\newcoreimg1");
		RunMainFrame ocrThread = new RunMainFrame(dirList);
		Thread ss = new Thread(ocrThread);
		ss.start();
	}

}
