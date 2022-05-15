package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Polynomial {
    public ArrayList<Integer> polynomial;

    public Polynomial(){
        this.polynomial = new ArrayList<Integer>();
    }

    public Polynomial(List<Integer> arr){
        this.polynomial = new ArrayList<Integer>(arr);
    }

    public void setPolynomial(List<Integer> polynomial) {
        this.polynomial.clear();
        this.polynomial.addAll(polynomial);
    }

    public ArrayList<Integer> getPolynomial() {
        return polynomial;
    }

    public int getRatio(int degree){
        try{
            return polynomial.get(degree);
        }
        catch (Exception ex){
            return 0;
        }
    }

    public int degree() {
        return polynomial.size();
    }

    public void add(int value){
        polynomial.add(value);
    }

    public Polynomial clone(){
        return new Polynomial(this.polynomial);
    }

    public boolean isZero(){
        return polynomial.size() == 1 && polynomial.get(0) == 0;
    }

    public void addElement(int degree, int value){
        polynomial.add(degree, value);
    }

    public void setElement(int degree, int value){
        polynomial.set(degree, value);
    }

    public Polynomial sum(Polynomial summand, int ring){
        ArrayList<Integer> amount = new ArrayList<Integer>();
        ArrayList<Integer> longer = polynomial.size() > summand.degree()? polynomial : summand.getPolynomial();
        for (int i = 0; i < Math.max(polynomial.size(), summand.degree()); i++){
            try{
                amount.add(polynomial.get(i) + summand.getRatio(i));
            }
            catch (Exception ex){
                amount.add(longer.get(i));
            }
        }
        return new Polynomial(amount).ring(ring);
    }

    public Polynomial multiply(Polynomial multiplier, int ring){
        ArrayList<Integer> composition = new ArrayList<>(Collections.nCopies((polynomial.size()-1)+(multiplier.degree()), 0));
        for(int i = 0; i < polynomial.size(); i++){
            for(int j = 0; j < multiplier.degree(); j++){
                composition.set(i+j, composition.get(i+j) + polynomial.get(i)*multiplier.getRatio(j));
            }
        }
        return new Polynomial(composition).ring(ring);
    }

    public void refresh(){
        for (int i = polynomial.size()-1; i >= 1; i--){
            if (polynomial.get(i) == 0)
                polynomial.remove(i);
            else
                return;
        }
        if (polynomial.size() == 0)
            polynomial.add(0);
    }

    public Polynomial ring(int abs){
        for (int i = 0; i < polynomial.size(); i++){
            if (polynomial.get(i) >= abs) {
                polynomial.set(i, polynomial.get(i) % abs);
                continue;
            }
            if (polynomial.get(i) < 0) {
                int value = polynomial.get(i) % abs;
                if (value != 0)
                    value += abs;
                polynomial.set(i, value);
            }
        }
        refresh();
        return new Polynomial(polynomial);
    }

    public void cut(int degree){
        try {
            polynomial.subList(degree + 1, polynomial.size()).clear();
            refresh();
        }
        catch (Exception ignored){}
    }

    @Override
    public boolean equals(Object obj) {
        return obj.equals(polynomial);
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        int size = polynomial.size();
        for(int i = size-1 ; i >= 0; i--){
            int cur = polynomial.get(i);
            if (cur == 0) {
                continue;
            }
            if (i == size-1)
                str.append(cur).append("x^").append(i);
            else if (cur>0)
                str.append("+").append(cur).append("x^").append(i);
            else
                str.append(cur).append("x^").append(i);
        }
        if (str.toString().equals(""))
            str.append(0);
        return str.toString();
    }
}
