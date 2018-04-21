
package localsearch.functions.basic;

import static java.lang.Math.abs;
import java.util.HashSet;
import localsearch.functions.sum.Sum;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

/**
 *
 * @author DungBachViet
 */
public class ABS extends AbstractInvariant implements IFunction{
    
    private int _value;
    private int _minValue;
    private int _maxValue;
 
    private VarIntLS[] _variables;
    private VarIntLS _x;
    private int _y;
    private LocalSearchManager _ls;
    
    public ABS(VarIntLS x, int y) {
        _x = x;
        _y = y;
        
        _ls = x.getLocalSearchManager();
        post();
    }
    
    private void post() {
        
        _variables = new VarIntLS[1];
        _variables[0] = _x;
        _value = abs(_x.getValue() - _y);
        
        _maxValue = Integer.MIN_VALUE;
        _minValue = Integer.MAX_VALUE;
        int constant = _y;
        for (int value = _x.getMinValue(); value <= _x.getMaxValue(); value++) {
            if (abs(value - constant) > _maxValue)
                _maxValue = abs(value - constant);
            
            if (abs(value - constant) < _minValue)
                _minValue = abs(value - constant);
        }
        
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
    public int getAssignDelta(VarIntLS x, int val) {
       
        return !(x == _x) ? 0 : abs(val - _y) - abs(_x.getValue() - _y);
    }

    @Override
    public int getSwapDelta(VarIntLS x, VarIntLS y) {
//        // TODO Auto-generated method stub
//        if ((!(x.IsElement(_x))) && (!(y.IsElement(_x)))) {
//            return 0;
//        }
//        return _f1.getSwapDelta(x, y) - _f2.getSwapDelta(x, y);

        return 0;
    }
    
    @Override
    public VarIntLS[] getVariables() {
        return _variables;
    }
    
    @Override
    public void propagateInt(VarIntLS x, int val) {
        System.out.println("*********");
        _value = abs(_x.getValue() - _y);
    }

    @Override
    public void initPropagate() {
        _value = abs(_x.getValue() - _y);
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        // TODO Auto-generated method stub
        return _ls;
    }

    public static void main(String[] args) {
        LocalSearchManager ls = new LocalSearchManager();
        VarIntLS[] x = new VarIntLS[3];
        for (int i = 0; i < 3; i++) {
           x[i] = new VarIntLS(ls, 0, 100);
           x[i].setValue(5);
        }
            
        
        ABS[] temp = new ABS[3];
        for (int i = 0; i < 3; i++) 
            temp[i] = new ABS(x[i], i);
        
        Sum obj = new Sum(temp);
        
    
        ls.close();
        System.out.println(obj.getValue());
        for (int i = 0; i < 3; i++) {
           x[i] = new VarIntLS(ls, 0, 100);
           x[i].setValuePropagate(10);
        }
        System.out.println(obj.getValue());
        
    }
    
    
}
