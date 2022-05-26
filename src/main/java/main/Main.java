package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        int ring = 5;
        int t = 5, j = 1;

        ArrayList<Polynomial> chains = new ArrayList(Collections.nCopies(125, new Polynomial(List.of(0))));
        ArrayList<Polynomial> alpha = new ArrayList<>(List.of(new Polynomial(List.of(1)), new Polynomial(List.of(0, 1))));
        Polynomial irreducible = new Polynomial(List.of(2, 3, 0, 1));//x^3+3*x+2
        Polynomial irr2 = irreducible.multiply(new Polynomial(List.of(2)), ring);//2x^3+x+4
        Polynomial irr3 = irreducible.multiply(new Polynomial(List.of(3)), ring);//3x^3+4x+1
        Polynomial irr4 = irreducible.multiply(new Polynomial(List.of(4)), ring);//4x^3+2x+3

        /*
         * вычисление поля alpha
         */
        for (int i = 2; i < 125; i++) {
            Polynomial newPol = alpha.get(1).multiply(alpha.get(i - 1), ring);
            switch (newPol.getRatio(3)) {
                case 1:
                    newPol = newPol.sum(irr4, ring);
                    break;
                case 2:
                    newPol = newPol.sum(irr3, ring);
                    break;
                case 3:
                    newPol = newPol.sum(irr2, ring);
                    break;
                case 4:
                    newPol = newPol.sum(irreducible, ring);
                    break;
                default:
                    break;
            }
            newPol.cut(3);
            alpha.add(newPol);
        }

        /*
         * создание цепочек и их вычисление
         */
        for (int i = 1; i < 124; i++) {
            if (!chains.get(i).equals(new Polynomial(List.of(0))))
                continue;
            int alphaDegree = i;
            int abs = 124;
            Polynomial composition = new Polynomial(List.of(1));
            Polynomial curAlpha;
            ArrayList<Integer> curChain = new ArrayList<>();
            ArrayList<Polynomial> task = new ArrayList<>();
            while (!curChain.contains(alphaDegree)) {
                curAlpha = alpha.get(alphaDegree);
                curChain.add(alphaDegree);
                task.add(alpha.get(alphaDegree));
                composition = composition.multiply(curAlpha, ring);
                alphaDegree = alphaDegree * ring;
                if (alphaDegree >= abs) {
                    alphaDegree = alphaDegree % abs;
                }
            }

            for (int q = 0; q < task.size(); q++) {
                task.set(q, task.get(q).multiply(new Polynomial(List.of(-1)), 5));
                curChain.set(q, alpha.indexOf(task.get(q)));
            }

            ArrayList<Polynomial> y = new ArrayList<Polynomial>(List.of(task.get(0), new Polynomial(List.of(1))));
            ArrayList<Polynomial> tmpResult, multiplier;
            for (int q = 1; q < task.size(); q++) {
                tmpResult = new ArrayList<>(Collections.nCopies(y.size() + 1, new Polynomial(List.of(0))));
                multiplier = new ArrayList<Polynomial>(List.of(task.get(q), new Polynomial(List.of(1))));
                for (int a = 0; a < y.size(); a++) {
                    for (int b = 0; b < multiplier.size(); b++) {
                        int index = alpha.indexOf(y.get(a)) + alpha.indexOf(multiplier.get(b));
                        index = index > 124 ? index % 124 : index;
                        if (tmpResult.get(a + b).isZero()) {
                            tmpResult.set(a + b, alpha.get(index));
                        } else {
                            tmpResult.set(a + b, alpha.get(index).sum(tmpResult.get(a + b), ring));
                        }
                    }
                }
                y = (ArrayList<Polynomial>) tmpResult.clone();
            }

            for (int q = 0; q < task.size(); q++) {
                task.set(q, task.get(q).multiply(new Polynomial(List.of(-1)), 5));
                curChain.set(q, alpha.indexOf(task.get(q)));
            }
            Polynomial yPol = new Polynomial();
            for (int b = 0; b < y.size(); b++) {
                yPol.addElement(b, y.get(b).getRatio(0));
            }
            for (int a = 0; a < curChain.size(); a++) {
                chains.set(curChain.get(a), yPol);
            }
        }
        chains.set(0, new Polynomial(List.of(-1, 1)));
        chains.set(chains.size() - 1, new Polynomial(List.of(-1, 1)));

        Polynomial csm = new Polynomial(List.of(1));
        ArrayList<Polynomial> checkList = new ArrayList<>();
        for (int i = j; i <= t * 2; i++) {
            if (checkList.contains(chains.get(i)))
                continue;
            csm = csm.multiply(chains.get(i), ring);
            checkList.add(chains.get(i));
        }

        System.out.println("g(x) = " + csm);

        /*
         * запрос v(x) для кодирования
         */
        Scanner in = new Scanner(System.in);
        System.out.print("Введите v(x) для кодирования.\n" +
                "Вводите коэффиценты начиная с x^0.\n" +
                "Ввод закончится, когда будет получено не целое число.\n");
        Polynomial v = new Polynomial();
        while (true) {
            try {
                int num = in.nextInt();
                v.add(num);
            } catch (Exception ex) {
                in.next();
                break;
            }
        }
        System.out.println("Вы ввели: " + v);
        System.out.println("c(x) = " + v.multiply(csm, ring) + "\n");

        /*
         * запрос v(x) для декода
         */
        System.out.print("Введите v(x) для декодирования.\n" +
                "Введите коэффиценты начиная с x^0.\n" +
                "Ввод закончится, когда будет получено не целое число.\n");
        v.setPolynomial(List.of());
        while (true) {
            try {
                int num = in.nextInt();
                v.add(num);
            } catch (Exception ex) {
                break;
            }
        }
        System.out.println("Вы ввели: " + v);

        /*
         * подсчет синдромов
         */
        alpha.add(new Polynomial(List.of(0)));//добавление в альфа нулевого многочлена, 125 степень - ноль
        ArrayList<Integer> syndromes = new ArrayList<>(Collections.nCopies(11, 0));//нулевая позиция не является синдромом
        for (int i = j; i < t * 2 + j; i++) {
            Polynomial newAlpha = new Polynomial(List.of(0));
            for (int b = 0; b < v.degree(); b++) {
                int degree = i * b;
                degree = degree > 124 ? degree % 124 : degree;
                newAlpha = newAlpha.sum(alpha.get(degree).multiply(new Polynomial(List.of(v.getRatio(b))), ring), ring);
            }
            syndromes.set(i - j + 1, alpha.indexOf(newAlpha));//если значние синдрома -1, то синдром равен нулю
        }

        /*
         * декодер Берлекэмпа-Месси
         */
        int delta = 0;//степень альфа
        ArrayList<Integer> T = new ArrayList<>();//позиция в массиве - степнь x, значение - степнь альфа
        ArrayList<Integer> B = new ArrayList<>(List.of(0));//позиция в массиве - степнь x, значение - степнь альфа
        ArrayList<Integer> lambda = new ArrayList<>(List.of(0));//позиция в массиве - степнь x, значение - степнь альфа
        ArrayList<ArrayList> lambdas = new ArrayList<>(List.of((ArrayList) lambda.clone()));//массив лямбд
        int L = 0;

        for (int r = 1; r < syndromes.size(); r++) {//1 1 3 3 0 2 0 2 4 2 2 0 1 2 0 1 1 1 1 3 2 1 4 1 4 2 a
            ArrayList<Integer> tmpResult = new ArrayList<>();//1 1 1 4 1 2 0 1 1 4 4 2 4 2 4 1 4 0 4 1 0 1 3 0 0 0 4 1 3 2 4 3 a

            //подсчет дельты
            for (int a = 0; a <= L; a++) {//перемножение, а - номер лямбды (j)
                if (syndromes.get(r - a) == 125)
                    tmpResult.add(125);
                else {
                    int degree = syndromes.get(r - a) + lambda.get(a);
                    degree = degree > 124 ? degree % 124 : degree;
                    tmpResult.add(a, degree);
                }
            }

            delta = tmpResult.get(0);
            for (int i = 0; i < L; i++) {//сумма для подсчета дельты
                delta = alpha.indexOf(alpha.get(delta).sum(alpha.get(tmpResult.get(i + 1)), ring));
            }

            if (delta == 125) {//если дельта равна нулю, то найден многочлен локаторов ошибки'
                B.add(0, 125);
                if (r == 2 * t)
                    break;
                continue;
            }

            //подсчет T(x)
            T.clear();
            int minusDelta = alpha.indexOf(alpha.get(delta).multiply(new Polynomial(List.of(-1)), ring));//индекс -дельта
            tmpResult.clear();
            tmpResult.addAll(Collections.nCopies(B.size() + 1, 125));
            for (int i = 0; i < B.size(); i++) {//подсчет правго слогаемого при T(x)
                if (B.get(i) == 125)//если B - ноль, то скип, т.к. в тмп уже нули
                    continue;
                int degree = B.get(i) + minusDelta;
                degree = degree > 124 ? degree % 124 : degree;
                tmpResult.set(i + 1, degree);
            }

            for (int i = 0; i < Math.max(tmpResult.size(), lambda.size()); i++) {
                try {
                    T.add(alpha.indexOf(alpha.get(tmpResult.get(i)).sum(alpha.get(lambda.get(i)), ring)));
                } catch (Exception ex) {
                    ArrayList<Integer> longer = tmpResult.size() > lambda.size() ? tmpResult : lambda;
                    T.add(longer.get(i));
                }
            }

            //проверка 2L ≤ r −1
            if (2 * L <= r - 1) {
                int deltaDegreeMinus = -delta + 124;
                B.clear();
                for (int i = 0; i < lambda.size(); i++) {//новое B
                    int degree = deltaDegreeMinus + lambda.get(i);
                    degree = degree > 124 ? degree % 124 : degree;
                    B.add(i, degree);
                }

                L = r - L;//новое кол-во ошибок
                lambda = (ArrayList) T.clone();
            } else {
                lambda = (ArrayList) T.clone();
                B.add(0, 125);
            }
            if (r == 2 * t)
                break;
        }



        if (L == 0) {
            System.out.println("v(x) = " + v);
            return;
        }

        ArrayList<Integer> errorsPosition = new ArrayList<Integer>();
        for (int i = 0; i < alpha.size() - 1; i++) {//степень проверяющего альфа
            Polynomial newAlpha = new Polynomial(List.of(0));
            for (int b = 0; b < lambda.size(); b++) {//степнь x
                int degree;
                if (lambda.get(b) == 125) {
                    continue;
                }
                degree = i * b + lambda.get(b);
                degree = degree > 124 ? degree % 124 : degree;
                newAlpha = newAlpha.sum(alpha.get(degree), ring);
            }
            if (newAlpha.isZero())
                errorsPosition.add(124 - i);
        }
        System.out.println("Степени a с ошибкой: " + errorsPosition + "\n");

        Matrix mat = new Matrix(Collections.nCopies(errorsPosition.size(), Collections.nCopies(errorsPosition.size(), 125)), alpha);
        for (int i = 0; i < mat.size(); i++) {
            for (int a = 0; a < mat.get(0).size(); a++) {
                int degree = errorsPosition.get(a) * (i + 1);
                degree = degree > 124 ? degree % 124 : degree;
                mat.setElement(i, a, degree);
            }
        }
        mat = mat.revers();
        
        Matrix syndromeMat = new Matrix();
        for (int i = 1; i <= errorsPosition.size(); i++) {
            syndromeMat.addLine(new ArrayList<>(List.of(syndromes.get(i))));
        }
        Matrix result = mat.multiply(syndromeMat);

        for (int i = 0; i < errorsPosition.size(); i++) {
            Polynomial errFix = new Polynomial(Collections.nCopies(errorsPosition.get(i) + 1, 0));
            errFix.setElement(errorsPosition.get(i), (alpha.get(result.getElement(i, 0))).getRatio(0));
            errFix = errFix.multiply(new Polynomial(List.of(-1)), ring);
            v = v.sum(errFix, ring);
        }

        System.out.println("v(x) = " + v);
    }
}
