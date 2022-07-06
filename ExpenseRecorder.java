import javax.naming.spi.DirStateFactory.Result;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import java.io.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.nio.file.*;
import java.util.stream.Stream;


public class ExpenseRecorder extends JFrame implements ActionListener{
    JLabel label = new JLabel();
    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();
    //自分で使ってみたい方は""の中身を入れてください 例）Users/gotoukenji/Desktop/
    String filePath = " ";
    //フレームを作る
    public ExpenseRecorder(String title){
        setTitle(title);
        setBounds(100, 100, 600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //メニューバー
        JMenuBar menuBar = new JMenuBar();
        //メニュー
        JMenu total = new JMenu("合計");
        JMenu record = new JMenu("記録");
        JMenu register = new JMenu("登録");
        //メニューアイテム
        JMenuItem monthTotal = new JMenuItem("月合計");
        JMenuItem sortTotal = new JMenuItem("種類別合計");
        JMenuItem allTotal = new JMenuItem("総合計");
        JMenuItem valueRecord = new JMenuItem("記録");
        JMenuItem sortRegister = new JMenuItem("種類登録");
        //メニューアイテムをメニューに追加して、それをメニューバーに追加してセットする
        total.add(monthTotal);
        total.add(sortTotal);
        total.add(allTotal);
        record.add(valueRecord);
        register.add(sortRegister);
        menuBar.add(total);
        menuBar.add(record);
        menuBar.add(register);
        setJMenuBar(menuBar);
        //ボタンが押されたことを認識
        monthTotal.addActionListener(this);
        sortTotal.addActionListener(this);
        allTotal.addActionListener(this);
        valueRecord.addActionListener(this);
        sortRegister.addActionListener(this);
        try{
            Path p = Path.of(filePath+"kind");
            Path p2 = Path.of(filePath+"kind/kind.txt");
            Path p3 = Path.of(filePath+"total");
            Files.createDirectories(p);
            Files.createFile(p2);
            Files.createDirectories(p3);
        }catch(IOException p){
            System.out.println("既にファイルが存在している");       
        }
    }
/*ボタンが押された時に発生するイベント
monthTotal→ m~
sortTotal→ s~
alLTotal→ a~
valueRecord→c~ 
sRegister→r~*/
    public void actionPerformed(ActionEvent e){
        //どのメニューアイテムボタンが押されたのかをチェック、パネルに追加されていたコンポーネントを削除し追加し直す
        String cmd = e.getActionCommand();
        panel1.removeAll();
        panel2.removeAll();
        label.setHorizontalAlignment(JLabel.CENTER);
        getContentPane().add(label,BorderLayout.PAGE_START);
        
        //月合計が押されたときの処理
        if(cmd.equals("月合計")){
            label.setText("月を入力してください");
            JTextField mText = new JTextField();
            mText.setColumns(10);
            panel1.add(mText);
            getContentPane().add(panel1,BorderLayout.CENTER);
            //エンターが押された時の処理
            class totalEnter implements ActionListener{
                public void actionPerformed(ActionEvent e){
                     //ファイル名を取得して入力したものと一致するものの中身を計算する
                    int count=0;
                    File mFile = new File(filePath+"total");
                    File[] mFileList = mFile.listFiles();
                    if(mFileList != null){
                        for(int i=0;i<mFileList.length;++i){
                            String mFilePath = filePath+"total/"+mText.getText()+".txt";
                            if(mFilePath.equals(mFileList[i].toString())){
                                try{
                                    FileReader fileReader = new FileReader(mFileList[i]);
                                    BufferedReader br = new BufferedReader(fileReader);
                                    String data ;
                                    try{
                                        int value=0;
                                        while((data = br.readLine())!=null){
                                            try{
                                                value += Integer.parseInt(data.substring(0, data.length()-3));
                                            }catch(NumberFormatException m){
                                                label.setText("エラーが発生しました");
                                                System.out.println("ファイルに問題あり(105)");
                                            }
                                        }
                                        br.close();
                                        String result = Integer.toString(value);
                                        label.setText(mText.getText()+"の合計は"+result+"円です");
                                    }catch(IOException m){
                                        label.setText("エラーが発生しました");
                                        System.out.println("ファイルに問題あり(113)");
                                    }
                                }catch(IOException m){
                                    label.setText("エラーが発生しました");
                                    System.out.println("ファイルに問題あり(117)");
                                }
                                ++count;
                            }
                        }
                        if(count==0){
                            label.setText("ファイルが見つかりませんでした。もう一度入力してください");
                        }
                    }
                    mText.setText("");
                }
            }
            //エンターが押されたときにアクションが起こるように追加
            mText.addActionListener(new totalEnter());
        //種類別が押されたときの処理
        }else if(cmd.equals("種類別合計")){
            label.setText("種類を入力してください　例)01");
            JTextField sText = new JTextField();
            sText.setColumns(10);
            panel1.add(sText);
            getContentPane().add(panel1,BorderLayout.CENTER);
            File sFile = new File(filePath+"total");
            File kFile = new File(filePath+"kind/kind.txt");
            File[] sList = sFile.listFiles();
            //エンターが押された時の処理
            //入力された数字と一致するものの合計を計算
            //入力された数字で登録されている名前のものをkindに代入
            class sortEenter implements ActionListener{
                public void actionPerformed(ActionEvent s){
                    if(sList != null){
                        int value=0;
                        String datas="";
                        String kinds="";
                        String kind="";
                        for(int i=0;i<sList.length;++i){
                                try{
                                    FileReader fileReaders1 = new FileReader(sList[i]);
                                    BufferedReader br1 = new BufferedReader(fileReaders1); 
                                    FileReader fileReader2 = new FileReader(kFile);
                                    BufferedReader br2 = new BufferedReader(fileReader2);
                                    try{
                                        while((datas = br1.readLine())!=null){
                                            if(datas.substring(datas.length()-2,datas.length()).equals(sText.getText())){
                                                try{
                                                    value += Integer.parseInt(datas.substring(0, datas.length()-3));
                                                }catch(NumberFormatException sort){
                                                    label.setText("エラーが発生しました");
                                                    System.out.println("ファイルの中身に数字以外のものがある(164)");
                                                }
                                            }
                                        }
                                        String result = Integer.toString(value);
                                        br1.close();
                                        while((kinds=br2.readLine())!=null){
                                            if(kinds.substring(0,2).equals(sText.getText())){
                                                kind = kinds.substring(3,kinds.length());
                                            }
                                        }
                                        br2.close();
                                        label.setText(kind+"の合計は"+result+"円です");
                                    }catch(IOException sort){
                                        label.setText("エラーが発生しました");
                                        System.out.println("ファイルが見つからないとか(179)");
                                    }
                                }catch(IOException sort){
                                    label.setText("エラーが発生しました");
                                    System.out.println("ファイルが見つからないとか(183)");
                                }
                        }
                    }
                }
            }
            //エンターが押されたときにイベントが発生するように追加
            sText.addActionListener(new sortEenter());
        //総合計が押されたときに発生するイベント
        }else if(cmd.equals("総合計")){
            File aFile = new File(filePath+"total");
            File[] fileList = aFile.listFiles();
            if(fileList != null){
                int value=0;
                String datas="";
                String result="";
                for(int i=0;i<fileList.length;++i){
                    try{
                        FileReader fileReaders = new FileReader(fileList[i]);
                        BufferedReader br = new BufferedReader(fileReaders); 
                        try{
                            while((datas = br.readLine())!=null){
                                try{
                                    value += Integer.parseInt(datas.substring(0, datas.length()-3));
                                }catch(NumberFormatException l){
                                    label.setText("エラーが発生しました");
                                    System.out.println("ファイルの中身に数字以外のものがある(188)");
                                }
                            }
                            result = Integer.toString(value);
                            br.close();
                        }catch(IOException all){
                            label.setText("エラーが発生しました");
                            System.out.println("ファイルが見つからないとか");
                        }
                    }catch(IOException a){
                        label.setText("エラーが発生しました");
                        System.out.println("ファイルが見つからないとか");
                    }
                }        
                label.setText("総合計は"+result+"円です");
            }
        //種類登録が押されたときに発生するイベント
        }else if(cmd.equals("種類登録")){
            label.setText("種類を登録します 例)01 肉類");
            String datas;
            JTextField rText = new JTextField();
            JTextArea rArea = new JTextArea();
            rText.setColumns(10);
            panel1.add(rText);
            panel2.add(rArea);
            getContentPane().add(panel1,BorderLayout.PAGE_END);
            getContentPane().add(panel2,BorderLayout.CENTER);
            File rFile = new File(filePath+"kind/kind.txt");
            try{
                FileReader fileReader= new FileReader(rFile);
                BufferedReader br = new BufferedReader(fileReader);
                while((datas=br.readLine())!=null){
                    rArea.append(datas+"\n");
                }
            }catch(IOException r){
                label.setText("エラーが発生しました");
                System.out.println("ファイルが見つからないとか");
            }
            //エンターが押された時の処理
            class rEnter implements ActionListener{
                public void actionPerformed(ActionEvent r){
                    String str = rText.getText();
                    rArea.append(str);
                    try{
                    FileWriter fileWriter = new FileWriter(rFile,true);
                    BufferedWriter bw = new BufferedWriter(fileWriter);
                    PrintWriter pw = new PrintWriter(bw);
                    pw.println(str);
                    pw.close();
                    rText.setText("");
                    }catch(IOException register){
                        label.setText("エラーが発生しました");
                        System.out.println("ファイルが見つからないとか");
                    }
                }
            } 
            rText.addActionListener(new rEnter());
        //記録が押されたときに発生するイベント
        }else if(cmd.equals("記録")){ 
            label.setText("年月を入力してください");
            String datas="";
            JTextField cText = new JTextField();
            JTextArea cArea = new JTextArea();
            cText.setColumns(10);
            panel1.add(cText);
            panel2.add(cArea);
            getContentPane().add(panel1,BorderLayout.PAGE_END);
            getContentPane().add(panel2,BorderLayout.CENTER);
            File sFile = new File(filePath+"kind/kind.txt");
            try{
                FileReader fileReader= new FileReader(sFile);
                BufferedReader br = new BufferedReader(fileReader);
                while((datas=br.readLine())!=null){
                    cArea.append(datas+"\n");
                }
            }catch(IOException r){
                label.setText("エラーが発生しました");
                System.out.println("ファイルが見つからないとか");
            }
            //エンターが押された時の処理
            class  cEnter implements ActionListener{
                int count=0;
                String path;
                public void actionPerformed(ActionEvent e){
                    if(count==0){
                    String time = cText.getText();
                    if(time.equals("F")){
                        panel1.remove(cText);
                        panel1.repaint();
                        label.setText("");
                    }else{
                        Path p1 = Path.of(filePath+"total/"+time+".txt");
                        path = p1.toString();
                    //ファイル作成
                        try{
                            Files.createFile(p1);
                            label.setText("値段 種類を入力してください 例）100 01");
                        }catch(IOException a){
                            label.setText("値段 種類を入力してください 例）100 01");
                        }
                        cText.setText("");
                        ++count;
                        }
                    }
                    else if(count==1){                        
                        //テキストの情報＝information
                        String information = cText.getText();
                        File sFile = new File(filePath+"kind/kind.txt");
                
                        if(information.equals("F")){
                            label.setText("");
                            panel1.remove(cText);
                            panel1.repaint();
                        }else{
                            int length = information.length();
                            try{
                                String value = cText.getText(0,length-3);
                                String kind="";
                                //ファイル書きこみ
                                File cFile = new File(path);
                                try{
                                    try{
                                        String kinds;
                                        FileReader fileReader = new FileReader(sFile);
                                        BufferedReader br = new BufferedReader(fileReader);
                                        while((kinds=br.readLine())!=null){
                                            if(information.substring(length-2, length).equals(kinds.substring(0,2))){
                                                kind = kinds.substring(3,kinds.length());
                                            }
                                        }
                                    }catch(IOException c){
                                        label.setText("エラーが発生しました");
                                        System.out.println("ファイルが見つからないとか");
                                    }                     
                                    FileWriter fileWriter = new FileWriter(cFile,true);
                                    BufferedWriter bw = new BufferedWriter(fileWriter);
                                    PrintWriter pw = new PrintWriter(bw);
                                    pw.println(information);
                                    label.setText(value+"円の"+kind+"を記録しました");
                                    cText.setText("");
                                    pw.close();
                                    }catch(IOException c){
                                    label.setText("エラーが発生しました");
                                    System.out.println("ファイルが見つからないとか");
                                }
                            }catch(BadLocationException c){
                                System.out.print("存在しない位置を参照してる");
                            }
                        }
                    }
                }
            }
            cText.addActionListener(new  cEnter());
        }
    }
    

    public static void main (String[] args){
        ExpenseRecorder frame = new ExpenseRecorder("money");
        frame.setVisible(true);
    }
}
