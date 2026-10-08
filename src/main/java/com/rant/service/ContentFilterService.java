package com.rant.service;

import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class ContentFilterService {

    private final List<Pattern> tier1Patterns = new ArrayList<>();
    private final List<Pattern> tier2Patterns = new ArrayList<>();
    private final List<Pattern> tier3Patterns = new ArrayList<>();

    public ContentFilterService() {
        initializeFilters();
    }

    private void initializeFilters() {
        // TIER 1 - HARD BLOCK
        List<String> tier1Words = Arrays.asList(
            "nigger", "nigga", "faggot", "fag", "tranny", "retard", "retarded", 
            "kike", "spic", "chink", "paki", "coon", "rape you", "rapist", 
            "kill yourself", "kys", "i will kill you", "i'll kill you", 
            "bomb threat", "shoot up", "gang rape", "molest", "pedo", "paedo", 
            "child porn", "cp",
            "madarchod", "madarchodh", "maderchod", "mc", "behenchod", "bhenchod", 
            "behnchod", "bhenchodh", "bc", "bhosdike", "bhosdika", "bhosadike", 
            "bhosdiwale", "bsdk", "randi", "raand", "randwa", "balatkar", 
            "rape kar dunga", "maar dunga", "jaan se maar", "tujhe maar dalunga", 
            "mar ja", "marja saale", "suicide kar le",
            "मादरचोद", "बहनचोद", "भोसड़ीके", "रंडी", "बलात्कार"
        );
        for (String word : tier1Words) {
            tier1Patterns.add(buildRegex(word));
        }

        // TIER 2 - MASK
        List<String> tier2Words = Arrays.asList(
            "fuck", "fucking", "fucker", "motherfucker", "mf", "shit", "bullshit", 
            "bitch", "bastard", "asshole", "dick", "dickhead", "cock", "pussy", 
            "cunt", "whore", "slut", "piss", "crap", "douche", "wanker", "twat", 
            "prick", "jackass", "dumbass",
            "chutiya", "chutiye", "chut", "chutiyapa", "gandu", "gaandu", "gand", 
            "gaand", "gand mara", "lund", "lauda", "loda", "lavda", "lodu", 
            "harami", "haramzada", "haramkhor", "haramzadi", "kamina", "kamine", 
            "kutta", "kutti", "kutte", "saala", "saale", "sala", "bhadwa", 
            "bhadva", "bhadwe", "chinal", "tatti", "tatte", "bakchod", "bakchodi", 
            "jhaatu", "jhatu", "lodey", "bewakoof", "ullu ka pattha", "gandmasti",
            "चूतिया", "गांडू", "हरामी", "हरामखोर", "कमीना", "कुत्ता", "साला", "बकचोद", "लंड"
        );
        for (String word : tier2Words) {
            tier2Patterns.add(buildRegex(word));
        }

        // TIER 3 - PII REGEX (Custom Patterns)
        tier3Patterns.add(Pattern.compile("(\\+91[\\-\\s]?)?[6-9]\\d{9}"));
        tier3Patterns.add(Pattern.compile("[\\w.+-]+@[\\w-]+\\.[\\w.]+"));
        tier3Patterns.add(Pattern.compile("\\b\\d{4}\\s?\\d{4}\\s?\\d{4}\\b"));
        tier3Patterns.add(Pattern.compile("https?://", Pattern.CASE_INSENSITIVE));
        tier3Patterns.add(Pattern.compile("@\\w+", Pattern.CASE_INSENSITIVE));
        tier3Patterns.add(Pattern.compile("t\\.me/", Pattern.CASE_INSENSITIVE));
        tier3Patterns.add(Pattern.compile("wa\\.me/", Pattern.CASE_INSENSITIVE));
    }

    private Pattern buildRegex(String word) {
        StringBuilder regex = new StringBuilder();
        
        // Match word boundary for English/Hinglish (Devanagari might not use \b perfectly but this works mostly)
        regex.append("\\b");
        
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c == ' ') {
                regex.append("[\\W_]+"); // Space maps to any non-word separator
            } else {
                regex.append(getCharRegex(c)).append("+");
                if (i < word.length() - 1 && word.charAt(i+1) != ' ') {
                    regex.append("[\\W_]*"); // Optional separator between letters
                }
            }
        }
        
        regex.append("\\b");
        return Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
    }

    private String getCharRegex(char c) {
        c = Character.toLowerCase(c);
        switch (c) {
            case 'a': return "[aA@4]";
            case 'i': return "[iI1!]";
            case 'o': return "[oO0]";
            case 's': return "[sS$5]";
            default:
                if (Character.isLetterOrDigit(c)) {
                    return "[" + Character.toLowerCase(c) + Character.toUpperCase(c) + "]";
                }
                return "\\" + c;
        }
    }

    public String filterAndMask(String body) {
        // 1. Check Tier 1 (Hard Block)
        for (Pattern p : tier1Patterns) {
            if (p.matcher(body).find()) {
                throw new IllegalArgumentException("Post rejected: Violates content policy (Tier 1).");
            }
        }

        // 2. Check Tier 3 (PII)
        for (Pattern p : tier3Patterns) {
            if (p.matcher(body).find()) {
                throw new IllegalArgumentException("Post rejected: Contains PII or Links (Tier 3).");
            }
        }

        // 3. Mask Tier 2
        String maskedBody = body;
        for (Pattern p : tier2Patterns) {
            maskedBody = p.matcher(maskedBody).replaceAll("****");
        }

        return maskedBody;
    }
}
