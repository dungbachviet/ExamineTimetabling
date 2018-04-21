package localsearch.model;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;

import core.BasicEntity;
import localsearch.model.VarIntLS;
import java.util.*;

public class LocalSearchManager {

    private int n;
    //private VarIntLS[] _x;
    private ArrayList<VarIntLS> _x;
    private Vector<AbstractInvariant> _invariants;
    private ConstraintSystem _S = null;
    //private Vector<TreeSet<BasicEntity>> _map;
    private HashMap<VarIntLS, TreeSet<AbstractInvariant>> _map;
    private boolean _closed;

    public LocalSearchManager() {
        n = 0;
        _invariants = new Vector<AbstractInvariant>();
        _x = new ArrayList<VarIntLS>();
        System.out.println("OpenCBLS library: by dungkhmt@gmail.com, Algorithms & Optimization LAB, SoICT, HUST, 2014-2015");
        _closed = false;
    }

    
    
    // Tại sao phải cần tới ID để làm gì?
    public void post(VarIntLS x) {

        x.setID(n++);
        _x.add(x);
    }
    
    
    // Chú ý rằng : Do đã có ConstraintSystem quản lý hay lưu giữa tất cả các ràng
    // buộc hiện có, nên phương thức này chỉ nhận các tham số như là Function và
    // SystemConstraint mà chẳng khi nào nhận tham số kiểu Constraint cả (vì constraint
    // đã nằm trong sự quản lý của SystemConstraint)
    public void post(AbstractInvariant e) {

        e.setID(n++);
        _invariants.add(e);
        
//        if (e instanceof ConstraintSystem)
//        {
//            _S = (ConstraintSystem) e;
//        }
    }
    
    // Bản chất thì post() này với post() cũng đều có cùng chức năng
    // nhưng post() ở trên tổng quát hơn vì tham số là AbstractInvariant,
    // trong khi ConstraintSystem lại kết thừa từ AbstractInvariant
    // Ta có thể gộp 2 post vào một nhờ việc thêm comment như trong post trên
    // Chú ý rằng : mảng _invariants không chỉ lưu cả Function mà còn lưu cả
    // ConstraintSystem (tác giả lưu ConstraintSystem ở cuối mảng)
    public void post(ConstraintSystem S) {
        if (_S != null) {
            System.out.println("LocalSearchManager::post(ConstraintSystem) EXCEPTION: A ConstraintSystem has already instantiated");
            assert (false);
        }
        _S = S;
        //_S.setID(n++);
    }
    
    // Vấn đề 1 :
    //Nếu biến x có tham gia vào một Constraint nào đó thì sao? Rõ ràng mảng 
    // _invariant bên trong LocalSeachManagement chỉ chứa các Function và
    // duy nhất một ConstraintSystem (quản lý toàn bộ function). Nên nếu có tham
    // gia vào một Constraint nào đó thì map.get(x) sẽ trả về tập các AbstractInvariant
    // mà trong đó có ConstraintSystem (vì ConstraintSystem kế thừa từ AbstractInvariant)
    // Vấn đề 2 : Do ở đây trong mỗi TreeSet<> lưu các phần tử theo thứ tự tăng dần của
    // Id, Id càng nhỏ sẽ cho biết rằng Function đó được tạo ra càng sớm và ngược lại 
    // khi Id lớn có nghĩa là phần tử đó được kết nạp vào càng muộn
    // Như vậy, ta cần phải lan truyền cho những function có id nhỏ trước (để tránh trường
    // hợp function lồng trong function thì rõ ràng function bị lồng ở lồng trong cùng 
    // phải được lan truyền trước ) !!!
    public void propagateInt(VarIntLS x, int val) {
        TreeSet<AbstractInvariant> s = _map.get(x);
        if (s == null) {
            return;
        }
        
    
        
        for (Invariant e : s) {
            e.propagateInt(x, val);
        }
    }
    
    // Thực hiện initPropagate cho toàn bộ function và constraint mà nó đang quản lý
    public void initPropagate() {
        //for (int i = 0; i < n; i++)
        for (int i = 0; i < _invariants.size(); i++) {
            _invariants.elementAt(i).initPropagate();
        }
    }

    public String name() {
        return "LocalSearchManager";
    }

    public void print() {
        System.out.println(name() + "::print");
        System.out.print("VarIntLS = ");
        for (int i = 0; i < _x.size(); i++) {
            System.out.println("_x[" + _x.get(i).getID() + "], ");
        }
        System.out.println();
        System.out.println("_invariants = ");
        for (int i = 0; i < _invariants.size(); i++) {
            System.out.println("_invariants[" + i + "] = " + "(id = " + _invariants.get(i).getID()
                    + ", name = " + _invariants.get(i).name() + ")");
        }

        System.out.println("Dependency = ");
        for (int i = 0; i < _x.size(); i++) {
            VarIntLS x = _x.get(i);
            System.out.print("x[" + x.getID() + "] defines: ");
            TreeSet<AbstractInvariant> T = _map.get(x);
            if (T == null) {
                System.out.println("Error:: This VarIntLS doesn't define any constraint");
            } else {
                for (AbstractInvariant invr : T) {
                    {
                        System.out.println(invr.name() + "[" + invr.getID() + "], ");
                    }
                }
            }
            System.out.println();
        }

    }

    public boolean closed() {
        return _closed;
    }

    public void close() {
        if (closed()) {
            return;
        }
        _closed = true;

        if (_S != null) {
            _S.setID(n++);
            _invariants.add(_S);
            _S.close();
        }
        
        _map = new HashMap<VarIntLS, TreeSet<AbstractInvariant>>();

        //for (int i = 0; i < n; i++) {
        for (int i = 0; i < _invariants.size(); i++) {
            VarIntLS[] s = _invariants.elementAt(i).getVariables();
            if (s != null) //for (VarIntLS e : s) {
            {
                for (int j = 0; j < s.length; j++) {
                    VarIntLS x = s[j];
                    if (_map.get(x) == null) {
                        _map.put(x, new TreeSet<AbstractInvariant>(new Compare()));
                    }
                    _map.get(x).add(_invariants.elementAt(i));
                }
            }
        }
        //System.out.println(name() + "::close");

        //print();
        initPropagate();

    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}

class Compare implements Comparator<BasicEntity> {

    @Override
    public int compare(BasicEntity o1, BasicEntity o2) {
        return o1.getID() - o2.getID();
    }

}
