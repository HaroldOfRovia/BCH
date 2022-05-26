package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Matrix {
    public ArrayList<ArrayList<Integer>> matrix;
    public ArrayList<Polynomial> alpha;

    public Matrix(){
        this.matrix = new ArrayList<ArrayList<Integer>>();
        this.alpha = new ArrayList<>();
    }

    public Matrix(ArrayList<ArrayList<Integer>> matrix, ArrayList<Polynomial> alpha){
        this.matrix = (ArrayList<ArrayList<Integer>>) matrix.clone();
        this.alpha = alpha;
    }

    public Matrix(List<List<Integer>> matrix, ArrayList<Polynomial> alpha){
        this.matrix = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < matrix.size(); i++){
            this.matrix.add(new ArrayList<Integer>(matrix.get(i)));
        }
        this.alpha = alpha;
    }

    public ArrayList<ArrayList<Integer>> getMatrix() {
        return matrix;
    }

    public int size(){
        return matrix.size();
    }

    public ArrayList<Integer> get(int i){
        return matrix.get(i);
    }

    public void addLine(ArrayList<Integer> line){
        matrix.add(line);
    }

    public int getElement(int i, int j){
        return this.matrix.get(i).get(j);
    }

    public void setElement(int i, int j, int element){
        this.matrix.get(i).set(j, element);
    }

    public Matrix clone(){
        ArrayList<ArrayList<Integer>> mat = new ArrayList();
        for(int i = 0; i < this.matrix.size(); i++){
            mat.add(new ArrayList<Integer>(this.matrix.get(i)));
        }
        return new Matrix(mat, (ArrayList<Polynomial>) alpha.clone());
    }

    public int removePos(int iPos, int jPos){
        int ij = this.matrix.get(iPos).get(jPos);
        for(int i = 0; i < this.matrix.size(); i++){
            for(int j = 0; j < this.matrix.get(i).size(); j++){
                if (j == jPos)
                    this.matrix.get(i).remove(j);
            }
        }
        this.matrix.remove(iPos);
        return ij;
    }

    public int detDegree(){
        int det = 125;
        if((this.matrix.size() == 1) && (matrix.get(0).size() == 1))
            return this.matrix.get(0).get(0);
        for(int j = 0; j < this.matrix.size(); j++){//по первой строке
            Matrix lessMat = clone();
            int firstSummand = lessMat.removePos(0, j);//удаленная позиция
            int minus = (int) Math.pow(-1, j);//знак
            int secondSummand = lessMat.detDegree();//результат меньшего определителя
            if((firstSummand == 125) || (secondSummand == 125))
                continue;
            firstSummand = alpha.indexOf(alpha.get(firstSummand).multiply(new Polynomial(List.of(minus)), 5));
            int tmp = firstSummand + secondSummand;
            tmp = tmp > 124 ? tmp % 124 : tmp;
            det = alpha.indexOf(alpha.get(det).sum(alpha.get(tmp), 5));
        }
        return det;
    }

    public Matrix transposed(){
        Matrix tr = new Matrix(Collections.nCopies(this.matrix.get(0).size(), Collections.nCopies(this.matrix.size(), 125)), alpha);
        for (int i = 0; i < matrix.size(); i++){
            for (int j = 0; j < matrix.get(0).size(); j++)
                tr.setElement(j, i, matrix.get(i).get(j));
        }
        return tr;
    }

    public Matrix revers(){
        Matrix rev = clone();
        if (rev.detDegree() == 125)
            return new Matrix(List.of(
                    List.of(125)), alpha);

        if (rev.size() == 1) {
            rev.setElement(0,0, 124-rev.getElement(0,0));
            return rev;
        }

        rev.transposed();
        Matrix result = rev.clone();
        for (int i = 0; i < rev.getMatrix().size(); i++){
            for (int j = 0; j < rev.getMatrix().get(i).size(); j++){
                Matrix copy = rev.clone();
                int posDegree = copy.removePos(i, j);
                int other = copy.detDegree();
                if ((posDegree == 125) || (other == 125)) {
                    result.setElement(i, j, 125);
                    continue;
                }
                result.setElement(j, i, alpha.indexOf(alpha.get(other).multiply(new Polynomial(List.of((int) Math.pow(-1, i+j))),5)));
            }
        }
        result = result.multiplyOnDegree(-clone().detDegree());
        return result;
    }

    public Matrix multiplyOnDegree(int inputDegree){
        Matrix result = clone();
        for (int i = 0; i < result.getMatrix().size(); i++) {
            for (int j = 0; j < result.getMatrix().get(0).size(); j++){
                if ((inputDegree == 125) || (result.getElement(i,j) == 125)){
                    result.setElement(i, j, 125);
                    continue;
                }
                int degree = result.getElement(i, j) + inputDegree;
                degree = degree > 124 ? degree % 124 : degree;
                degree = degree < 0 ? degree + 124 : degree;
                result.setElement(i,j, degree);
            }
        }
        return result;
    }

    public Matrix multiply(Matrix multiplier){
        Matrix result = new Matrix(Collections.nCopies(this.matrix.size(), Collections.nCopies(multiplier.getMatrix().get(0).size(), 125)), alpha);
        for (int i = 0; i < result.getMatrix().size(); i++){
            for (int j = 0; j < result.getMatrix().get(0).size(); j++){
                for (int a = 0; a < multiplier.getMatrix().size(); a++){
                    if ((this.matrix.get(i).get(a) == 125) || (multiplier.getElement(a, j) == 125))
                        continue;
                    int degree = this.matrix.get(i).get(a) + multiplier.getElement(a, j);
                    degree = degree > 124 ? degree % 124 : degree;
                    result.setElement(i, j, alpha.indexOf(alpha.get(result.getElement(i, j)).sum(alpha.get(degree), 5)));
                }
            }
        }
        return result;
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < this.matrix.size(); i++){
            str.append(this.matrix.get(i).toString()).append("\n");
        }
        return str.toString();
    }
}
