/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AutoResultProcessing;

/**
 *
 * @author PETER-PC
 */
public class TestClass {
    
    public static void main(String[] args) {
        System.out.println("CSC308,CSC306 matches pattern [\\S]{6}, :"+"CSC308,CSC306".matches("[\\S]{6}(,).*"));
        System.out.println("CSC204,CSC302,CSC202 pattern [\\S]{6}, :"+"CSC204,CSC302,CSC202".matches("[\\S]{6},.*"));
        System.out.println("CSC204 pattern [\\S]{6}, :"+"CSC204".matches("[\\S]{6},.*"));
    }
}
