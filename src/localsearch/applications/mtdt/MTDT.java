package localsearch.applications.mtdt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.max_min.Max;
import localsearch.functions.max_min.Min;
import localsearch.functions.occurrence.Occurrence;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.functions.element.*;
import core.*;

public class MTDT {
    // Lưu độ phù hợp giữa sinh viên và giáo sư
    public int[][] match;
    // Số lượng sinh viên = số lượng hội đồng
    public int n;
    // Lưu index của giáo sư hướng dẫn cho sinh viên i 
    public int[] sup;// sup[i] is the supervisor of student i
    // Lưu mãID của giáo sư hướng dẫn cho sinh viên i (thông tin thô)
    public int[] rawsup;

    // số giáo sư vòng 1
    public int m1;
    // số giáo sư vòng 2
    public int m2;

    // Lưu mãID của các giáo sư vòng 1 (có m1 giáo sư)
    public int[] intp;
    // Lưu mãID của các giáo sư vòng 2 (có m1 giáo sư)
    public int[] extp;
    // Lưu số kíp (trong 1 ngày)
    public int nbSlots;
    // Lưu số phòng có sẵn
    public int nbRooms;

    // Dùng để chuyển đổi qua lại giữa mã của giáo sư với index của giáo sư
    // Sự chuyển đổi này là cần thiết (vì ta cần dùng index đó cho mảng)
    public HashMap<Integer, Integer> map;

    // Mỗi giáo sư chỉ được làm nhiều nhất 3 lần ở vị trí examiner
    public int lambda = 3;// max number of times each professor is a examiner
    // Model
    LocalSearchManager ls;
    ConstraintSystem S;

    // Lưu 5 thành viên trong hội đồng cho mỗi sinh viên
    VarIntLS[][] x_p;
    // Lưu slot (kíp) bảo vệ cho mỗi sinh viên
    VarIntLS[] x_s;
    // Lưu phòng bảo vệ cho mỗi sinh viên
    VarIntLS[] x_r;

    // ????????
    IFunction obj1;
    IFunction obj2;
    IFunction[] occ;
    IFunction[] match1;
    IFunction[] match2;

    public void readData(String filename) {
        try {
            File file;
            file = new File(filename);
            Scanner scan = new Scanner(file);
            String line = scan.nextLine(); // đọc hết dòng
            // line = scan.nextLine();
            System.out.println("Line = " + line);

            // số hội đồng = số sinh viên sẽ bảo vệ
            n = scan.nextInt();
            line = scan.nextLine(); // đọc hết dòng
            line = scan.nextLine();
            System.out.println("Line = " + line);
            map = new HashMap<Integer, Integer>();
            sup = new int[n];
            rawsup = new int[n];
            for (int i = 0; i < n; i++) {
                int sid = scan.nextInt();// student id
                rawsup[i] = scan.nextInt(); // supervisor của sinh viên
                System.out.print(sid + " " + rawsup[i] + " ");

                // 5 thành viên hội đồng + kíp (slot) + phòng
                for (int j = 0; j < 7; j++) {
                    int v = scan.nextInt();// do not used
                    System.out.print(v + " ");
                }
                System.out.println();
            }
            line = scan.nextLine();
            line = scan.nextLine();
            // tổng số các giáo sư vòng 1
            m1 = scan.nextInt();
            intp = new int[m1];// internal professors
            for (int i = 0; i < m1; i++) {
                intp[i] = scan.nextInt();
            }

            line = scan.nextLine();
            line = scan.nextLine();
            // tổng số các giáo sư vòng 2
            m2 = scan.nextInt();
            extp = new int[m2];// external professors
            for (int i = 0; i < m2; i++) {
                extp[i] = scan.nextInt();
            }

            // map để lưu tương ứng : biến giáo sư và chỉ số (từ 0 tới m1+m2-1)
            for (int i = 0; i < m1; i++) {
                map.put(intp[i], i);
            }
            for (int i = 0; i < m2; i++) {
                map.put(extp[i], m1 + i);
            }

            // rawsup[i] là mã số của supervisor cho sinh viên i
            // sup[i] là index của supervisor (0->m1+m2-1) cho sinh viên i
            for (int i = 0; i < n; i++) {
                sup[i] = map.get(rawsup[i]);
            }

            line = scan.nextLine();
            line = scan.nextLine();

            // Lưu độ phù hợp của từng sinh viên đối với từng giáo sư
            match = new int[n][m1 + m2];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m1 + m2; j++) {
                    scan.nextInt(); // đây là chỉ số i của sinh viên, cái này ta biết rồi
                    int u = scan.nextInt(); // lấy mã số của giáo sư
                    int v = scan.nextInt(); // độ phù hợp của giáo sư với sinh viên i
                    //System.out.println("read match, u = " + u + ", v = " + v);
                    // Nếu trong map không có giáo sư này
                    if (map.get(u) == null) {
                        continue;
                    }
                    int u1 = map.get(u); // Lấy index của giáo sư có mã số u
                    match[i][u1] = v; // lưu lại độ phù hợp giữa sv i và giáo sư u1
                    //System.out.println("match[" + i + "][" + u1 + "] = " + match[i][u1]);
                }

            }
            line = scan.nextLine();
            line = scan.nextLine();
            nbSlots = scan.nextInt(); // Lấy số slot (số kíp)
            nbRooms = scan.nextInt(); // Lấy số phòng có sẵn dành cho bảo vệ luận án

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // Hàm này tạo ra một mảng 1 chiều từ mảng 2 chiều 
    public VarIntLS[] copyTo1DArray(VarIntLS[][] x) {
        int r = x.length;
        int c = x[0].length;
        //System.out.println("r = " + r + ", c = " + c); System.exit(-1);

        VarIntLS[] y = new VarIntLS[r * c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                y[i * c + j] = x[i][j];
            }
        }
        return y;
    }
    
    // Lấy cột c trong tất cả các hàng (của mảng 2 chiều)
    public VarIntLS[] copyColumn(VarIntLS[][] x, int c) {

        VarIntLS[] y = new VarIntLS[x.length];
        for (int i = 0; i < y.length; i++) {
            y[i] = x[i][c];
        }
        return y;
    }
    
    // Lấy hàng r trong ma trận 2 chiều
    public VarIntLS[] copyRow(VarIntLS[][] x, int r) {
        VarIntLS[] y = new VarIntLS[x[0].length];
        for (int i = 0; i < y.length; i++) {
            y[i] = x[r][i];
        }
        return y;
    }

    public void stateModel() {
        ls = new LocalSearchManager();
        S = new ConstraintSystem(ls);

        // chỉ định 5 thành viên trong hội đồng cho sinh viên i
        x_p = new VarIntLS[n][5];
        x_s = new VarIntLS[n]; // Slot (kíp) bảo vệ của sinh viên i
        x_r = new VarIntLS[n]; // phòng bảo vệ của sinh viên i

        for (int i = 0; i < n; i++) {
            // Lưu các giáo sư vòng 2 (external professor)
            x_p[i][0] = new VarIntLS(ls, m1, m1 + m2 - 1);
            // Lưu giáo sư vòng 1 (internal professor)
            x_p[i][1] = new VarIntLS(ls, 0, m1 - 1);
            // Các vị trí cho chủ tịch, thư ký, ủy viên
            x_p[i][2] = new VarIntLS(ls, 0, m1 - 1);
            x_p[i][3] = new VarIntLS(ls, 0, m1 - 1);
            x_p[i][4] = new VarIntLS(ls, m1, m1 + m2 - 1);
        }
        
        // Khởi tạo cho biến slot (lưu kíp bảo vệ) và phòng sẽ bảo vệ
        for (int i = 0; i < n; i++) {
            x_s[i] = new VarIntLS(ls, 0, nbSlots - 1);
            x_r[i] = new VarIntLS(ls, 0, nbRooms - 1);
        }
        
        // Nếu 1 giáo sư ở 2 hội đồng, thì 2 hội đồng phải khác kíp bảo vệ
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                // Xét từng thành viên trong hội đồng thứ 1
                for (int p1 = 0; p1 < 5; p1++) {
                    // Xét từng thành viên cho hội đồng thứ 2
                    for (int p2 = 0; p2 < 5; p2++) {
                        S.post(new Implicate(
                                new IsEqual(x_p[i][p1], x_p[j][p2]),
                                new NotEqual(x_s[i], x_s[j])));
                    }
                }
            }
        }
        
        // Trong mỗi hội đồng các thành viên phải khác nhau
        for (int i = 0; i < n; i++) {
            VarIntLS[] y = copyRow(x_p, i);
            S.post(new AllDifferent(y));
            //for (int p1 = 0; p1 < 4; p1++) {
            //for (int p2 = p1 + 1; p2 < 5; p2++) {
            //	S.post(new NotEqual(x_p[i][p1], x_p[i][p2]));
            //}
            //}
        }
        
        // Nếu 2 hội đồng bảo vệ cùng 1 phòng thì kíp bảo vệ phải khác nhau
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                S.post(new Implicate(new IsEqual(x_r[i], x_r[j]),
                        new NotEqual(x_s[i], x_s[j])));
            }
        }
        
        // Giáo sư hướng dẫn của mỗi sv không được nằm trong ban hội đồng của sv đó
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 5; j++) {
                S.post(new NotEqual(x_p[i][j], sup[i]));
            }
        }
        
        
        // Số lần mà 1 giáo sư đảm nhiệm vị trí examinator 1 ko quá lambda lần
        VarIntLS[] y = copyColumn(x_p, 1);
        for (int i = 0; i < m1; i++) {
            IFunction o = new Occurrence(y, i);
            S.post(new LessOrEqual(o, lambda));
        }
        
        // Số lần mà 1 giáo sư đảm nhiệm vị trí examinator 2 ko quá lambda lần
        y = copyColumn(x_p, 0);
        for (int i = m1; i < m1 + m2; i++) {
            IFunction o = new Occurrence(y, i);
            S.post(new LessOrEqual(o, lambda));
        }
        
        // Hình như là đếm số lần xuất hiện của giáo sư i trong tất cả các hội đồng
        y = copyTo1DArray(x_p);
        occ = new IFunction[m1 + m2];
        for (int i = 0; i < m1 + m2; i++) {
            occ[i] = new Occurrence(y, i);
        }
        
        // Số lần xuất hiện lớn nhất và nhỏ nhất
        IFunction oMax = new Max(occ);
        IFunction oMin = new Min(occ);
        
        // Hiệu giữa max và min ==> Hình như là ta cần phải minimize (giảm)
        // hiệu này càng nhỏ càng tốt
        obj1 = new FuncMinus(oMax, oMin);
        
        // ??? match1 và match2 có phải là tổng độ phù hợp của các giáo sư ở vòng 1
        // và các giáo sư ở vòng 2 đối với sinh viên i
        match1 = new IFunction[n];
        match2 = new IFunction[n];
        for (int i = 0; i < n; i++) {
            match1[i] = new Element(match, i, x_p[i][0]);
            match2[i] = new Element(match, i, x_p[i][1]);
        }
        // ???? obj2 này có ý nghĩa là gì? Tại sao lại lấy hiệu của độ phù hợp
        // giữa các giáo sư vòng 1 và giữa các giáo sư vòng 2 với toàn bộ sv
        obj2 = new FuncMinus(0, new FuncPlus(new Sum(match1), new Sum(match2)));

        ls.close();

    }

    public void solve() {
        localsearch.search.TabuSearch ts = new localsearch.search.TabuSearch();
        ts.search(S, 20, 300, 1000000, 200);
        
        // Đây là search có thêm ràng buộc obj1 (tức làm cân bằng số lần xuất hiện của các giáo sư)
        ts.searchMaintainConstraintsMinimize(obj1, S, 20, 30, 100000, 200);
        
        // ??? Đây là các giúp truyền vào nhiều sự ràng buộc về Minimize
        // ??? Hàm này sẽ tối ưu có tính đến 2 mục tiêu obj1 và obj2
        IFunction[] tf = new IFunction[1];
        tf[0] = obj1;
        ts.searchMaintainConstraintsFunctionMinimize(obj2, tf, S, 20, 30, 100000, 200);
        
        // In ra kết quả sau khi đã tìm kiếm tối ưu
        for (int i = 0; i < n; i++) {
            System.out.println(i + " : " + rawsup[i] + " "
                    + extp[x_p[i][0].getValue() - m1] + " "
                    + intp[x_p[i][1].getValue()] + " "
                    + intp[x_p[i][2].getValue()] + " "
                    + intp[x_p[i][3].getValue()] + " "
                    + extp[x_p[i][4].getValue() - m1] + " " + x_s[i].getValue()
                    + " " + x_r[i].getValue() + ", match1 = " + match1[i].getValue() + ", match2 = " + match2[i].getValue());
        }
        
        // In ra số lần xuất hiện của từng giáo sư trong mỗi hội đồng
        for (int i = 0; i < m1; i++) {
            System.out.println("Occ[" + intp[i] + "] = " + occ[i].getValue());
        }
        for (int i = m1; i < m1 + m2; i++) {
            System.out.println("Occ[" + extp[i - m1] + "] = " + occ[i].getValue());
        }
        
        // ??? In ra giá trị của các mục tiêu (kết quả cuối cùng)
        // Các giá trị này có ý nghĩa như thế nào??? 
        System.out.println("obj1 = " + obj1.getValue() + ", obj2 = " + obj2.getValue());
    }

    public void testBatch(String filename, int nbTrials) {
        MTDT A = new MTDT();
        A.readData(filename);
        
        // nbTrials : number of trials (số lần chạy thử)
        // Mảng này lưu thời gian cho mỗi lần chạy thử
        double[] t = new double[nbTrials];
        double avg_t = 0;
        for (int k = 0; k < nbTrials; k++) {
            double t0 = System.currentTimeMillis();
            A.stateModel();
            A.solve();
            t[k] = (System.currentTimeMillis() - t0) * 0.001;
            // Lưu tổng thời gian cho tất cả các lần chạy thử
            avg_t += t[k];
        }
        
        // Trung bình thời gian cho từng lần chạy thử
        avg_t = avg_t * 1.0 / nbTrials;
        System.out.println("Time = " + avg_t);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        /*
		MainApp A = new MainApp();
		A.readData("data\\MasterThesisDefenseTimeTabling\\jury-data-8-4-2.txt");
		double t0 = System.currentTimeMillis();
		A.stateModel();
		A.solve();
		double t = System.currentTimeMillis() - t0;
		t = t * 0.001;
		System.out.println("Time = " + t);
         */
        MTDT A = new MTDT();
        A.testBatch("src\\data\\MTDT\\jury-data-19-4-5.txt", 1);
    }

}
