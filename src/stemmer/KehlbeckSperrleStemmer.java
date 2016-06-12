package stemmer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KehlbeckSperrleStemmer implements Stemmer {
    private final Logger logger = LogManager.getLogger(KehlbeckSperrleStemmer.class);

    @Override
    public String[] stem(final String[] tokens) {
        String[] stems = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            stems[i] = this.stem(tokens[i]);
        }
        return stems;
    }

    /* (non-Javadoc)
     * @see stemmer.Stemmer#stem(java.lang.String)
     */
    @Override
    public String stem(String token) {
        if (token.length() <= 2) {
            return token;
        }

        token = token.toLowerCase();

        String remainder;
        boolean applied = false;
        // Step 1a)
        remainder = token.substring(0, token.length() - 2);
        if (token.endsWith("sses") && Stemmer.getMeasure(remainder) > 0) {
            token = remainder;
            applied = true;
        } else {
            if (token.endsWith("ies") && Stemmer.getMeasure(remainder) > 0) {
                token = remainder;
                applied = true;
            }
        }
        remainder = token.substring(0, token.length() - 1);
        if (token.endsWith("s") && !token.endsWith("ss") && Stemmer.getMeasure(remainder) > 0) {
            token = remainder;
        }

        applied = false;
        boolean appliedSecondOrThirdRule = false;
        // Step 1b)
        if (token.length() > 3) {
            remainder = token.substring(0, token.length() - 3);
            if (token.endsWith("eed") && Stemmer.getMeasure(remainder) > 0) {
                token = remainder;
                applied = true;
            } else {
                if (token.endsWith("ing") && Stemmer.containsVowel(remainder)) {
                    token = remainder;
                    appliedSecondOrThirdRule = true;
                    applied = true;
                }
            }
        }

        if (token.length() > 2) {
            remainder = token.substring(0, token.length() - 2);
            if (token.endsWith("ed") && Stemmer.containsVowel(remainder)) {
                token = remainder;
                appliedSecondOrThirdRule = true;
            }
        }

        if (appliedSecondOrThirdRule) {
            if (token.endsWith("at") || token.endsWith("bl") || token.endsWith("iz")) {
                token = token + "e";
            } else if (Stemmer.endsWithDoubleConsonant(token) && !(token.endsWith("l") || token.endsWith("s") || token.endsWith("z"))) {
                token = token.substring(0, token.length() - 1);
            } else if (Stemmer.getOMeasure(token) && Stemmer.getMeasure(token) == 1) {
                token += 'e';
            }
        }

        // Step 1c)
        if (Stemmer.containsVowel(token) && token.endsWith("y") && token.length() > 1) {
            token = token.substring(0, token.length() - 1);
            token += 'i';
        }

        applied = false;
        // Step 2
        if (token.length() > 2) {
            char penultimate = token.charAt(token.length() - 2);
            switch (penultimate) {
                case 'a':
                    if (token.length() > 7) {
                        remainder = token.substring(0, token.length() - 7);
                        if (token.endsWith("ational") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "ate";
                            applied = true;
                        }
                    }
                    if (token.length() > 6 && !applied) {
                        remainder = token.substring(0, token.length() - 6);
                        if (token.endsWith("tional") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "tion";
                        }
                    }
                    break;
                case 'c':
                    if (token.length() > 4) {
                        remainder = token.substring(0, token.length() - 4);
                        if (token.endsWith("enci") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "ence";
                        } else if (token.endsWith("anci") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "ance";
                        }
                    }
                    break;
                case 'e':
                    if (token.length() > 4) {
                        remainder = token.substring(0, token.length() - 4);
                        if (token.endsWith("izer") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "ize";
                        }
                    }
                    break;
                case 'g':
                    if (token.length() > 4) {
                        remainder = token.substring(0, token.length() - 4);
                        if (token.endsWith("logi") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "log";
                        }
                    }
                    break;
                case 'l':
                    remainder = token.substring(0, token.length() - 2);
                    if (token.endsWith("bli") && Stemmer.getMeasure(remainder) > 0) {
                        token = remainder + "ble";
                        break;
                    }
                    if (token.length() > 3) {
                        if (token.endsWith("li")) {
                            char c = token.charAt(token.length() - 3);
                            if (c == 'c' || c == 'd' || c == 'e' || c == 't' || c == 'g' || c == 'h' || c == 'm' || c == 'r' || c == 'k') {
                                token = remainder;
                                break;
                            }
                        }
                        remainder = token.substring(0, token.length() - 3);
                        if (token.endsWith("eli") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "e";
                        }
                        if (token.length() > 4) {
                            remainder = token.substring(0, token.length() - 4);
                            if (token.endsWith("alli") && Stemmer.getMeasure(remainder) > 0) {
                                token = remainder + "al";
                            }
                            if (token.length() > 5) {
                                remainder = token.substring(0, token.length() - 5);
                                if (token.endsWith("ousli") && Stemmer.getMeasure(remainder) > 0) {
                                    token = remainder + "ous";
                                }
                            }
                        }
                    }
                    break;
                case 'o':
                    if (token.length() > 7) {
                        remainder = token.substring(0, token.length() - 7);
                        if (token.endsWith("ization") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "ize";
                            applied = true;
                        }
                    }
                    if (token.length() > 5 && !applied) {
                        remainder = token.substring(0, token.length() - 5);
                        if ((token.endsWith("ation") || token.endsWith("ator")) && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "ate";
                        }
                    }
                    break;
                case 's':
                    if (token.length() > 5) {
                        remainder = token.substring(0, token.length() - 5);
                        if (token.endsWith("alism") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "al";
                        }
                        if (token.length() > 7) {
                            remainder = token.substring(0, token.length() - 7);
                            if (token.endsWith("iveness") && Stemmer.getMeasure(remainder) > 0) {
                                token = remainder + "ive";
                            }
                            if (token.endsWith("fulness") && Stemmer.getMeasure(remainder) > 0) {
                                token = remainder + "ful";
                            }
                            if (token.endsWith("ousness") && Stemmer.getMeasure(remainder) > 0) {
                                token = remainder + "ous";
                            }
                        }
                    }
                    break;
                case 't':
                    if (token.length() > 5) {
                        remainder = token.substring(0, token.length() - 5);
                        if (token.endsWith("aliti") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "al";
                        }
                        if (token.endsWith("iviti") && Stemmer.getMeasure(remainder) > 0) {
                            token = remainder + "ive";
                        }
                        if (token.length() > 6) {
                            remainder = token.substring(0, token.length() - 6);
                            if (token.endsWith("biliti") && Stemmer.getMeasure(remainder) > 0) {
                                token = remainder + "ble";
                            }
                        }
                    }
                    break;
            }
            if (token.length() > 7) {
                remainder = token.substring(0, token.length() - 7);
                if (token.endsWith("ational") && Stemmer.getMeasure(remainder) > 0) {
                    token = remainder + "ate";
                    applied = true;
                }
            }
            if (token.length() > 6 && !applied) {
                remainder = token.substring(0, token.length() - 6);
                if (token.endsWith("tional") && Stemmer.getMeasure(remainder) > 0) {
                    token = remainder + "tion";
                }
            }
        }

        applied = false;
        // Step 3
        if (token.length() > 3) {
            remainder = token.substring(0, token.length() - 3);
            if (token.endsWith("ful") && Stemmer.getMeasure(remainder) > 0) {
                token = remainder;
            }
            if (token.length() > 4) {
                remainder = token.substring(0, token.length() - 4);
                if (token.endsWith("ness") && Stemmer.getMeasure(remainder) > 0) {
                    token = remainder;
                }
                if (token.endsWith("ical") && Stemmer.getMeasure(remainder) > 0) {
                    token = remainder + "ic";
                }
            }
            if (token.length() > 5) {
                remainder = token.substring(0, token.length() - 5);
                if ((token.endsWith("icate") || token.endsWith("iciti")) && Stemmer.getMeasure(remainder) > 0) {
                    token = remainder + "ic";
                }
                if (token.endsWith("alize") && Stemmer.getMeasure(remainder) > 0) {
                    token = remainder + "al";
                }
                if (token.endsWith("ative") && Stemmer.getMeasure(remainder) > 0) {
                    token = remainder;
                }
            }
        }

        applied = false;
        // Step 4
        if (token.length() > 2) {
            remainder = token.substring(0, token.length() - 2);
            if ((token.endsWith("al") || token.endsWith("er") || token.endsWith("ic") || token.endsWith("ou")) &&
                    Stemmer.getMeasure(remainder) > 1) {
                token = remainder;
            }
            if (token.length() > 5) {
                remainder = token.substring(0, token.length() - 5);
                if (token.endsWith("ement") && Stemmer.getMeasure(remainder) > 1) {
                    token = remainder;
                    applied = true;
                }
            }
        }

        if (!applied && token.length() > 4) {
            remainder = token.substring(0, token.length() - 4);
            if ((token.endsWith("ance") || token.endsWith("ence") || token.endsWith("able") || token.endsWith("ible") ||
                    token.endsWith("ment")) && Stemmer.getMeasure(remainder) > 1) {
                token = remainder;
                applied = true;
            }
        }

        if (!applied && token.length() > 3) {
            remainder = token.substring(0, token.length() - 3);
            if ((token.endsWith("ant") || token.endsWith("ent") || token.endsWith("ism") || token.endsWith("ate") ||
                    token.endsWith("iti") || token.endsWith("ous") || token.endsWith("ive") || token.endsWith("ize")) &&
                    Stemmer.getMeasure(remainder) > 1) {
                token = remainder;
            }
            if (token.endsWith("ion") && remainder.endsWith("s") && remainder.endsWith("t") && Stemmer.getMeasure(remainder) > 1) {
                token = remainder;
            }
        }

        applied = false;
        // Step 5
        remainder = token.substring(0, token.length() - 1);
        if (token.endsWith("e")) {
            if (Stemmer.getMeasure(remainder) > 1 || (Stemmer.getMeasure(remainder) == 1 && !Stemmer.getOMeasure(remainder)))
                token = remainder;
        }

        applied = false;
        // Step 5b)
        if (Stemmer.getMeasure(remainder) > 1 && token.endsWith("l") && Stemmer.endsWithDoubleConsonant(token)) {
            token = remainder;
        }

        return token;
    }
}
