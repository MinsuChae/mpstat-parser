import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class FileParser implements Runnable{
	private File file;
	private HashMap<String, HashMap<String,String>> memory=new HashMap<String, HashMap<String,String>>();
	private Thread t;
	public FileParser(File _file){
		this.file = _file;
		t=new Thread(this);
		t.start();
		//�����ļ��� �����Ǹ� ���ķ� �Ľ��� ó��
	}
	public String getValue(String wantCPU,String rowName){
		try {
			//������� �Ľ��ϱ� ������ �Ľ��� ���ϴ� �����Ͱ� �Ǿ��ִ��� �ľ� �Ұ�
			//�׷��� ������ �Ľ��� �� �� ������ ����� ó��
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<String,String> submemory = memory.get(wantCPU);
		if(submemory==null)
			return null;
		else
			return submemory.get(rowName);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedReader reader=null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return;
		}
		
		String str;
		String[] rowNames = null;
		String[] values;
		try {
			while((str=reader.readLine())!=null){
				//header �� ã��
				rowNames = str.split("[ \t]+");
				str=reader.readLine().trim();
				if(str==null){
					if(reader!=null)
						reader.close();
					return;
				}else if(!str.equals(""))
					break;
			}
			
			int cpuIndex=-1;
			for(int i=0;i<rowNames.length;i++){
				if(rowNames[i].toLowerCase().equals("cpu"))
					cpuIndex=i;
			}
			//do while ���� ������ �տ� header �� ã�� ���� �����ٱ��� �̸� �о��� �����̴�.
			do{
				values = str.split("[ \t]+");
				if(values.length==0)
					continue;
				int diff = rowNames.length-values.length;
				//mpstat ���� ��Ȥ ��� �κа� �� �����ϴ� �κ��� �ʵ� ���� ���� ���� ��찡 �־� ����
				
				HashMap<String, String> map=memory.get(values[cpuIndex-diff]);
				if(map==null){
					map = new HashMap<String,String>();
					memory.put(values[cpuIndex-diff], map);
				}
				for(int i=cpuIndex+1-diff;i<values.length;i++){
					map.put(rowNames[i+diff], values[i]);
				}
			}while((str=reader.readLine())!=null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(reader!=null){
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
