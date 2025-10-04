import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Password Strength Analyzer
 * A program that analyzes password strength and provides recommendations
 * 
 * Features:
 * - Checks password length, complexity, and common patterns
 * - Provides strength score and recommendations
 * - Interactive CLI interface
 * 
 * @author Pranav Verma
 * @version 1.0
 */
public class PasswordStrengthAnalyzer {
    
    private static final String[] COMMON_PASSWORDS = {
        "password", "123456", "password123", "admin", "qwerty",
        "letmein", "welcome", "monkey", "1234567890", "abc123"
    };
    
    private static final String[] WEAK_PATTERNS = {
        "1234", "abcd", "qwer", "asdf", "zxcv"
    };
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PasswordStrengthAnalyzer analyzer = new PasswordStrengthAnalyzer();
        
        System.out.println("🔐 PASSWORD STRENGTH ANALYZER 🔐");
        System.out.println("=====================================");
        
        while (true) {
            System.out.print("\nEnter a password to analyze (or 'quit' to exit): ");
            String password = scanner.nextLine();
            
            if (password.equalsIgnoreCase("quit")) {
                System.out.println("Thank you for using Password Strength Analyzer!");
                break;
            }
            
            PasswordResult result = analyzer.analyzePassword(password);
            analyzer.displayResult(result);
        }
        
        scanner.close();
    }
    
    public PasswordResult analyzePassword(String password) {
        PasswordResult result = new PasswordResult(password);
        
        // Check various criteria
        result.hasMinLength = password.length() >= 8;
        result.hasUppercase = Pattern.compile("[A-Z]").matcher(password).find();
        result.hasLowercase = Pattern.compile("[a-z]").matcher(password).find();
        result.hasNumbers = Pattern.compile("[0-9]").matcher(password).find();
        result.hasSpecialChars = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]").matcher(password).find();
        result.isCommonPassword = isCommonPassword(password);
        result.hasWeakPattern = hasWeakPattern(password);
        result.hasRepeatingChars = hasRepeatingCharacters(password);
        
        // Calculate strength score
        result.strengthScore = calculateStrengthScore(result);
        result.strengthLevel = getStrengthLevel(result.strengthScore);
        
        // Generate recommendations
        result.recommendations = generateRecommendations(result);
        
        return result;
    }
    
    private boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();
        for (String common : COMMON_PASSWORDS) {
            if (lowerPassword.equals(common) || lowerPassword.contains(common)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasWeakPattern(String password) {
        String lowerPassword = password.toLowerCase();
        for (String pattern : WEAK_PATTERNS) {
            if (lowerPassword.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasRepeatingCharacters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && 
                password.charAt(i + 1) == password.charAt(i + 2)) {
                return true;
            }
        }
        return false;
    }
    
    private int calculateStrengthScore(PasswordResult result) {
        int score = 0;
        
        // Length scoring
        if (result.password.length() >= 12) score += 25;
        else if (result.password.length() >= 8) score += 15;
        else score += 5;
        
        // Character variety scoring
        if (result.hasUppercase) score += 15;
        if (result.hasLowercase) score += 15;
        if (result.hasNumbers) score += 15;
        if (result.hasSpecialChars) score += 20;
        
        // Penalty for weak characteristics
        if (result.isCommonPassword) score -= 30;
        if (result.hasWeakPattern) score -= 15;
        if (result.hasRepeatingChars) score -= 10;
        
        return Math.max(0, Math.min(100, score));
    }
    
    private String getStrengthLevel(int score) {
        if (score >= 80) return "VERY STRONG";
        else if (score >= 60) return "STRONG";
        else if (score >= 40) return "MODERATE";
        else if (score >= 20) return "WEAK";
        else return "VERY WEAK";
    }
    
    private String generateRecommendations(PasswordResult result) {
        StringBuilder recommendations = new StringBuilder();
        
        if (!result.hasMinLength) {
            recommendations.append("• Use at least 8 characters (12+ recommended)\n");
        }
        if (!result.hasUppercase) {
            recommendations.append("• Add uppercase letters (A-Z)\n");
        }
        if (!result.hasLowercase) {
            recommendations.append("• Add lowercase letters (a-z)\n");
        }
        if (!result.hasNumbers) {
            recommendations.append("• Add numbers (0-9)\n");
        }
        if (!result.hasSpecialChars) {
            recommendations.append("• Add special characters (!@#$%^&*)\n");
        }
        if (result.isCommonPassword) {
            recommendations.append("• Avoid common passwords and dictionary words\n");
        }
        if (result.hasWeakPattern) {
            recommendations.append("• Avoid sequential patterns (1234, abcd, qwerty)\n");
        }
        if (result.hasRepeatingChars) {
            recommendations.append("• Avoid repeating characters (aaa, 111)\n");
        }
        
        if (recommendations.length() == 0) {
            recommendations.append("• Your password looks great! Consider using a password manager.");
        }
        
        return recommendations.toString();
    }
    
    private void displayResult(PasswordResult result) {
        System.out.println("\n📊 ANALYSIS RESULTS");
        System.out.println("==================");
        System.out.println("Password: " + maskPassword(result.password));
        System.out.println("Length: " + result.password.length() + " characters");
        System.out.println();
        
        // Strength indicator
        System.out.printf("Strength Score: %d/100 [%s]\n", result.strengthScore, result.strengthLevel);
        printStrengthBar(result.strengthScore);
        System.out.println();
        
        // Criteria checklist
        System.out.println("✅ CRITERIA CHECKLIST:");
        System.out.println(getCheckmark(result.hasMinLength) + " Minimum 8 characters");
        System.out.println(getCheckmark(result.hasUppercase) + " Contains uppercase letters");
        System.out.println(getCheckmark(result.hasLowercase) + " Contains lowercase letters");
        System.out.println(getCheckmark(result.hasNumbers) + " Contains numbers");
        System.out.println(getCheckmark(result.hasSpecialChars) + " Contains special characters");
        System.out.println(getCheckmark(!result.isCommonPassword) + " Not a common password");
        System.out.println(getCheckmark(!result.hasWeakPattern) + " No weak patterns");
        System.out.println(getCheckmark(!result.hasRepeatingChars) + " No repeating characters");
        System.out.println();
        
        // Recommendations
        System.out.println("💡 RECOMMENDATIONS:");
        System.out.println(result.recommendations);
    }
    
    private String maskPassword(String password) {
        if (password.length() <= 2) return "**";
        return password.charAt(0) + "*".repeat(password.length() - 2) + password.charAt(password.length() - 1);
    }
    
    private String getCheckmark(boolean condition) {
        return condition ? "✅" : "❌";
    }
    
    private void printStrengthBar(int score) {
        int barLength = 20;
        int filled = (score * barLength) / 100;
        
        System.out.print("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                if (score >= 80) System.out.print("█");
                else if (score >= 60) System.out.print("▓");
                else if (score >= 40) System.out.print("▒");
                else System.out.print("░");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println("]");
    }
}

/**
 * Data class to hold password analysis results
 */
class PasswordResult {
    String password;
    boolean hasMinLength;
    boolean hasUppercase;
    boolean hasLowercase;
    boolean hasNumbers;
    boolean hasSpecialChars;
    boolean isCommonPassword;
    boolean hasWeakPattern;
    boolean hasRepeatingChars;
    int strengthScore;
    String strengthLevel;
    String recommendations;
    
    public PasswordResult(String password) {
        this.password = password;
    }
}