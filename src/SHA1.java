import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

/**
 * Created by niewinskip on 2016-10-27.
 */
public class SHA1 extends JFrame  {
    public static final int WORD_LENGTH = 32;
    public static final int NUMBER_OF_WORDS = 80;
    public static final int BINARY_BLOCK_SIZE = 512;

    private static final String H0 = "01100111010001010010001100000001";
    private static final String H1 = "11101111110011011010101110001001";
    private static final String H2 = "10011000101110101101110011111110";
    private static final String H3 = "00010000001100100101010001110110";
    private static final String H4 = "11000011110100101110000111110000";

    private static String A_WORD;
    private static String B_WORD;
    private static String C_WORD;
    private static String D_WORD;
    private static String E_WORD;
    private static String F_WORD = "";
    private static String K_VAR = "";

    public static int WORD1_INDEX = 3;
    public static int WORD2_INDEX = 8;
    public static int WORD3_INDEX = 14;
    public static int WORD4_INDEX = 16;

    private static JFrame mainFrame;
    private JTextField textField;
    JPanel panel;
    JButton button1;
    JLabel label;

   public SHA1() {
       mainFrame = new JFrame("SHA-1");
       mainFrame.setSize(400,180);
       mainFrame.setLayout(new GridLayout(3, 1));

       textField = new JTextField("Enter text here..");
       button1 = new JButton("Calculate");
       label = new JLabel();
//       statusLabel.setSize(350,100);
//       mainFrame.addWindowListener(new WindowAdapter() {
//           public void windowClosing(WindowEvent windowEvent){
//               System.exit(0);
//           }
//       });
       panel = new JPanel();
       panel.setLayout(new FlowLayout());

       mainFrame.add(textField);
       mainFrame.add(label);
       mainFrame.add(button1);

       mainFrame.setVisible(true);
       button1.addActionListener(new ActionListener() {
                                     @Override
                                     public void actionPerformed(ActionEvent e) {
                                         String msg = textField.getText();
                                         String out = startSHA1(msg);
                                         label.setText(out);
                                     }
                                 }
       );

//        startSHA1(msg);
   }

    private String startSHA1(String msg) {
        // Prepare data blocks
        System.out.println("Text: " + msg);
        String binary = stringToBin(msg);
        DataBlocksCollection dataBlocksCollection = new DataBlocksCollection(BINARY_BLOCK_SIZE, binary);
        dataBlocksCollection.printData();

        // Extend each 512 data block into 80 x 32 words.
        for (char[] dataBlock : dataBlocksCollection.getDataBlocksCollection()) {
            A_WORD = H0;
            B_WORD = H1;
            C_WORD = H2;
            D_WORD = H3;
            E_WORD = H4;
            F_WORD = "";

            int i = 16;
            char[] words = new char[WORD_LENGTH * NUMBER_OF_WORDS];
            //copy
            for (int l = 0; l < dataBlock.length; l++) {
                words[l] = dataBlock[l];
            }

            // Extend
            while (i <= 79) {
                char[] word1 = new char[32];
                char[] word2 = new char[32];
                char[] word3 = new char[32];
                char[] word4 = new char[32];
                int o = 0;
                for (int x = (i - WORD1_INDEX) * 32; x <= (i - WORD1_INDEX) * 32 + 31; x++) {
                    word1[o] = words[x];
                    o++;
                }
                o = 0;
                for (int x = (i - WORD2_INDEX) * 32; x <= (i - WORD2_INDEX) * 32 + 31; x++) {
                    word2[o] = words[x];
                    o++;
                }
                o = 0;
                for (int x = (i - WORD3_INDEX) * 32; x <= (i - WORD3_INDEX) * 32 + 31; x++) {
                    word3[o] = words[x];
                    o++;
                }
                o = 0;
                for (int x = (i - WORD4_INDEX) * 32; x <= (i - WORD4_INDEX) * 32 + 31; x++) {
                    word4[o] = words[x];
                    o++;
                }
                // XOR all
                for (int x = 0; x < 32; x++) {
                    word2[x] = XOROperatinon(word1[x], word2[x]);
                }
                for (int x = 0; x < 32; x++) {
                    word3[x] = XOROperatinon(word3[x], word2[x]);
                }
                for (int x = 0; x < 32; x++) {
                    word4[x] = XOROperatinon(word3[x], word4[x]);
                }
                // ROT left
                char c = word4[0];
                for (int x = 0; x < 31; x++) {
                    word4[x] = word4[x + 1];
                }
                word4[31] = c;
                // Add new word
                o = 0;
                for (int x = i * 32; x <= i * 32 + 31; x++) {
                    words[x] = word4[o];
                    o++;
                }
                i++;
            }

            for (int x = 0; x < words.length; x++) {
                if (x % 32 == 0) {
                    System.out.println("\nWord: " + (x / 32));
                }
                System.out.print(words[x]);
            }

            // 4 Functions, 0-19, 20-39, 40-59, 60-79
            StringBuilder sb;
            System.out.println("\n\n********************************\nGenerating hash code:\n********************************\n");
            int x = 0;
            while (x < 80) {
                sb = new StringBuilder();
                for (int ii = 0 + x * 32; ii < x * 32 + 32; ii++) {
                    sb.append(words[ii]);
                }
                if (x >= 0 && x <= 19) {
                    function1();
                } else if (x >= 20 && x <= 39) {
                    function2();
                } else if (x >= 40 && x <= 59) {
                    function3();
                } else {
                    function4();
                }
                crossingFunction(sb.toString());
                x++;
            }
            System.out.println("\n\n********************************\nHash code:\n********************************\n");
            presentOutput();
        }
        return presentOutput();
    }

    //(B AND C) or (!B AND D), for 0-19
    private void function1() {
        StringBuilder bANDc = new StringBuilder();
        for (int x = 0; x < B_WORD.length(); x++) {
            bANDc.append(ANDOperatinon(B_WORD.charAt(x), C_WORD.charAt(x)));
        }
        StringBuilder NOTbANDd = new StringBuilder();
        for (int x = 0; x < B_WORD.length(); x++) {
            NOTbANDd.append(ANDOperatinon(NOTOperatinon(B_WORD.charAt(x)), D_WORD.charAt(x)));
        }
        StringBuilder output = new StringBuilder();
        for (int x = 0; x < bANDc.toString().length(); x++) {
            output.append(OROperatinon(bANDc.toString().charAt(x), NOTbANDd.toString().charAt(x)));
        }
        F_WORD = output.toString();
        K_VAR = "01011010100000100111100110011001";
    }

    //  B XOR C XOR D, for 20 - 39
    private void function2() {
        StringBuilder bXORc = new StringBuilder();
        for (int x = 0; x < B_WORD.length(); x++) {
            bXORc.append(XOROperatinon(B_WORD.charAt(x), C_WORD.charAt(x)));
        }
        StringBuilder output = new StringBuilder();
        for (int x = 0; x < D_WORD.length(); x++) {
            output.append(XOROperatinon(bXORc.toString().charAt(x), D_WORD.charAt(x)));
        }
        F_WORD = output.toString();
        K_VAR = "01101110110110011110101110100001";
    }

    //  (B AND C) OR (B AND D) OR (C AND D), for 40 - 59
    private void function3() {
        StringBuilder bANDc = new StringBuilder();
        for (int x = 0; x < B_WORD.length(); x++) {
            bANDc.append(ANDOperatinon(B_WORD.charAt(x), C_WORD.charAt(x)));
        }
        StringBuilder bANDd = new StringBuilder();
        for (int x = 0; x < B_WORD.length(); x++) {
            bANDd.append(ANDOperatinon(B_WORD.charAt(x), D_WORD.charAt(x)));
        }
        StringBuilder cANDd = new StringBuilder();
        for (int x = 0; x < C_WORD.length(); x++) {
            cANDd.append(ANDOperatinon(C_WORD.charAt(x), D_WORD.charAt(x)));
        }
        StringBuilder or1 = new StringBuilder();
        for (int x = 0; x < bANDc.toString().length(); x++) {
            or1.append(OROperatinon(bANDc.toString().charAt(x), bANDd.toString().charAt(x)));
        }
        StringBuilder output = new StringBuilder();
        for (int x = 0; x < or1.toString().length(); x++) {
            output.append(OROperatinon(or1.toString().charAt(x), cANDd.toString().charAt(x)));
        }
        F_WORD = output.toString();
        K_VAR = "10001111000110111011110011011100";
    }

    //  B XOR C XOR D, for 60 - 79
    private void function4() {
        StringBuilder bXORc = new StringBuilder();
        for (int x = 0; x < B_WORD.length(); x++) {
            bXORc.append(XOROperatinon(B_WORD.charAt(x), C_WORD.charAt(x)));
        }
        StringBuilder output = new StringBuilder();
        for (int x = 0; x < D_WORD.length(); x++) {
            output.append(XOROperatinon(bXORc.toString().charAt(x), D_WORD.charAt(x)));
        }
        F_WORD = output.toString();
        K_VAR = "11001010011000101100000111010110";
    }

    //    (A left rotate 5) + F + E + K + (the current word).
    private void crossingFunction(String currentWord) {
        String temp = ROTLeftFunction(A_WORD, 5);
        temp = new BinaryAdder().addBinary(temp, F_WORD);
        temp = new BinaryAdder().addBinary(temp, E_WORD);
        temp = new BinaryAdder().addBinary(temp, K_VAR);
        temp = new BinaryAdder().addBinary(temp, currentWord);
        temp = temp.substring(temp.length() - 32);

        E_WORD = D_WORD;
        D_WORD = C_WORD;
        C_WORD = ROTLeftFunction(B_WORD, 30);
        B_WORD = A_WORD;
        A_WORD = temp;
    }

    private String presentOutput() {
        String hash0 = new BinaryAdder().addBinary(H0, A_WORD);
        hash0 = hash0.substring(hash0.length() - 32);
        String hash1 = new BinaryAdder().addBinary(H1, B_WORD);
        hash1 = hash1.substring(hash1.length() - 32);
        String hash2 = new BinaryAdder().addBinary(H2, C_WORD);
        hash2 = hash2.substring(hash2.length() - 32);
        String hash3 = new BinaryAdder().addBinary(H3, D_WORD);
        hash3 = hash3.substring(hash3.length() - 32);
        String hash4 = new BinaryAdder().addBinary(H4, E_WORD);
        hash4 = hash4.substring(hash4.length() - 32);
        System.out.println("H0: " + hash0);
        System.out.println("H1: " + hash1);
        System.out.println("H2: " + hash2);
        System.out.println("H3: " + hash3);
        System.out.println("H4: " + hash4);

        String output = hash0 + hash1 + hash2 + hash3 + hash4;
        output = convertBinaryToHex(output);
        System.out.println("\nHASH CODE:\n" + output);
        return output;
    }

    private String convertBinaryToHex(String bin) {
        StringBuilder hexOutput = new StringBuilder();
        int sum = 0;
        int multi = 8;
        for (int x = 0; x < bin.length(); x++) {
            if (bin.charAt(x) == '1') {
                sum += multi;
            }
            multi /= 2;

            if ((x + 1) % 4 == 0) {
                if (sum >= 0 && sum <= 9) {
                    hexOutput.append((char) (sum + 48));
                } else {
                    switch (sum) {
                        case 10:
                            hexOutput.append('A');
                            break;
                        case 11:
                            hexOutput.append('B');
                            break;
                        case 12:
                            hexOutput.append('C');
                            break;
                        case 13:
                            hexOutput.append('D');
                            break;
                        case 14:
                            hexOutput.append('E');
                            break;
                        case 15:
                            hexOutput.append('F');
                            break;
                    }
                }
                sum = 0;
                multi = 8;
            }
        }
        return hexOutput.toString();
    }

    private String ROTLeftFunction(String s, int rot) {
        if (rot < 1) {
            return s;
        }
        String temp = s.substring(rot);
        String temp2 = s.substring(0, rot);
        return temp + "" + temp2;
    }

    private char XOROperatinon(char a, char b) {
        if ((a == '0' && b == '0') || (a == '1' && b == '1')) {
            return '0';
        } else {
            return '1';
        }
    }

    private char OROperatinon(char a, char b) {
        if (a == '0' && b == '0') {
            return '0';
        } else {
            return '1';
        }
    }

    private char ANDOperatinon(char a, char b) {
        if (a == '1' && b == '1') {
            return '1';
        } else {
            return '0';
        }
    }

    private char NOTOperatinon(char a) {
        return a == '1' ? '0' : '1';
    }

    public  String stringToBin(String s) {
        s = s.trim();
        String binary = new BigInteger(s.getBytes()).toString(2);
        while (binary.length() % 8 != 0) {
            binary = "0" + binary;
        }
        System.out.println("As binary: " + binary);
        return binary;
    }
}
