package common;

import java.util.Scanner;

/**
 * 사용자 입력 처리 유틸리티
 */
public class InputUtil {
    private Scanner scanner;

    public InputUtil() {
        this.scanner = new Scanner(System.in);
    }

    public String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ 숫자를 입력해주세요.");
            }
        }
    }

    public boolean getYesNoInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt + " (y/N): ").toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no") || input.isEmpty()) {
                return false;
            } else {
                System.out.println("❌ 'y' 또는 'n'을 입력해주세요.");
            }
        }
    }

    public void pressEnterToContinue() {
        System.out.print("\n계속하려면 Enter를 누르세요...");
        scanner.nextLine();
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}