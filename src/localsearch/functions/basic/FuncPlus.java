package localsearch.functions.basic;

import java.util.HashSet;

import localsearch.model.*;

public class FuncPlus extends AbstractInvariant implements IFunction {

    private int _value;
    private int _minValue;
    private int _maxValue;
    private IFunction _f1;
    private IFunction _f2;
    private VarIntLS[] _x;
    private LocalSearchManager _ls;

    public FuncPlus(IFunction f1, IFunction f2) {
        this._f1 = f1;
        this._f2 = f2;
        _ls = f1.getLocalSearchManager();
        post();
    }

    public FuncPlus(IFunction f, VarIntLS x) {
        _f1 = f;
        // Tạo ra đối tượng IFunction từ đối tượng VarIntLS
        _f2 = new FuncVarConst(x);
        _ls = f.getLocalSearchManager();
        post();
    }

    // ??? Ở đây tác giả tạo ra một đối tượng Function để chứa giá trị val
    // Mặc dù giá trị val (là một biến nguyên thủy hoặc một hằng số truyền vào) thì
    // không bị thay đổi theo thời gian
    public FuncPlus(IFunction f, int val) {
        _f1 = f;
        _f2 = new FuncVarConst(f.getLocalSearchManager(), val);
        _ls = f.getLocalSearchManager();
        post();
    }

    public FuncPlus(VarIntLS x, VarIntLS y) {
        _f1 = new FuncVarConst(x);
        _f2 = new FuncVarConst(y);
        _ls = x.getLocalSearchManager();
        post();
    }

    public FuncPlus(VarIntLS x, int val) {
        _f1 = new FuncVarConst(x);
        _f2 = new FuncVarConst(x.getLocalSearchManager(), val);
        _ls = x.getLocalSearchManager();
        post();
    }

    // Phương thức này được thực hiện ngay sau constructor (cuối constructor sẽ gọi đến
    //  nó. Nó sẽ thực hiện các chuẩn bị cơ bản (về mảng lưu trữ,...)
    private void post() {

        // Lấy toàn bộ các biến có trong 2 function _f1, và _f2.
        // Việc sử dụng Set để đảm bảo tính duy nhất của đối tượng (nghĩa là
        // không để cho cùng một đối tượng bị xuất hiện tới tận 2 lần)
        HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
        VarIntLS[] x1 = _f1.getVariables();
        VarIntLS[] x2 = _f2.getVariables();
        if (x1 != null) {
            for (int i = 0; i < x1.length; i++) {
                _S.add(x1[i]);
            }
        }
        if (x2 != null) {
            for (int i = 0; i < x2.length; i++) {
                _S.add(x2[i]);
            }
        }

        // Lấy toàn bộ đối tượng từ trong Set để lưu vào một mảng _x (chuyên chứa
        // các đối tượng kiểu VarIntLS)
        _x = new VarIntLS[_S.size()];
        int i = 0;
        for (VarIntLS e : _S) {
            _x[i] = e;
            i++;
        }

        // Tính giá trị hiện tại, tính maxValue, tính minValue
        _value = _f1.getValue() + _f2.getValue();
        _maxValue = _f1.getMaxValue() + _f2.getMaxValue();
        _minValue = _f1.getMinValue() + _f2.getMinValue();

        // gọi phương thức post của ls để ls lưu lại đối tượng FuncPlus này
        // để có thể kiểm soát được sau này
        _ls.post(this);
    }

    @Override
    public int getMinValue() {
        return _minValue;
    }

    @Override
    public int getMaxValue() {
        return _maxValue;
    }

    @Override
    public int getValue() {
        return _value;
    }

    @Override
    public VarIntLS[] getVariables() {
        return _x;
    }

    @Override
    // Thực hiện gán thử giá trị val cho đối tượng x bên trong đối tượng FuncPlus này.
    // Ta cần phải kiểm tra x này có nằm trong tập _x[] chứa toàn bộ các biến của 
    // FuncPlus này hay không? Nếu có thì sự thay đổi sẽ bằng tổng của 2 sự thay đổi
    // trên 2 function thành phần của nó
    public int getAssignDelta(VarIntLS x, int val) {
        return (!(x.IsElement(_x))) ? 0 : _f1.getAssignDelta(x, val) + _f2.getAssignDelta(x, val);
    }

    @Override
    public int getSwapDelta(VarIntLS x, VarIntLS y) {
        // TODO Auto-generated method stub
        if ((!(x.IsElement(_x))) && (!(y.IsElement(_x)))) {
            return 0;
        }
        return _f1.getSwapDelta(x, y) + _f2.getSwapDelta(x, y);
    }

    @Override
    // ??? Đây chính là thao tác giúp cập nhật một đối tượng VarIntLS trong quá
    // trình di chuyển cục bộ. Lúc này, ls sẽ tìm ra tập các function và constraint
    // chứa đối tượng VarIntLS rồi tính toán lại value cho từng function và từng
    // constraint. Vấn đề là : Trường hợp Function lồng trong Function thì thứ tự
    // tính value cho function nào trước sẽ ảnh hưởng tới function sau 
    // ??? ==> Giải quyết ntn????
    public void propagateInt(VarIntLS x, int val) {
        _value = _f1.getValue() + _f2.getValue();
    }

    @Override
    // Phương thức này sẽ được gọi ngay sau khi đóng model (ls.close()) để thực hiện
    // tính giá trị cho từng function và vi phạm cho từng constraint ở thời điểm đầus
    public void initPropagate() {
        _value = _f1.getValue() + _f2.getValue();
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        // TODO Auto-generated method stub
        return _ls;
    }

    public String name() {
        return "FuncPlus";
    }

    @Override
    public boolean verify() {
        // TODO Auto-generated method stub
        return false;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        LocalSearchManager _ls = new LocalSearchManager();
        VarIntLS[] x = new VarIntLS[10];
        for (int i = 0; i < x.length; i++) {
            x[i] = new VarIntLS(_ls, 0, 100);
        }
        x[3].setValue(10);
        IFunction[] f = new IFunction[x.length];
        for (int i = 0; i < f.length; i++) {
            f[i] = new FuncPlus(x[i], 10);
        }
        _ls.close();
        System.out.println(f[3].getValue());

    }
}
