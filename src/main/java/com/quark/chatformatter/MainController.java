package com.quark.chatformatter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {
    @FXML
    private Button blackButton, darkBlueButton, darkGreenButton, darkAquaButton, darkRedButton, darkPurpleButton,
            goldButton, grayButton, darkGrayButton, blueButton, greenButton, aquaButton, redButton, lightPurpleButton,
            yellowButton, whiteButton, underlineButton, boldButton, italicButton, strikethroughButton, obfuscatedButton,
            chatCodeButton, motdButton, copyButton, resetButton;

    @FXML
    private InlineCssTextArea previewTextArea;

    @FXML
    private TextArea textCodeArea;

    private Timeline obfuscationTimeline;

    private final List<ObfuscationSegment> obfuscationSegments = new ArrayList<>();

    private boolean isMOTDMode = false;

    private static class ObfuscationSegment {
        int start;
        int end;
        String style;
        int length;

        ObfuscationSegment(int start, int end, String style) {
            this.start = start;
            this.end = end;
            this.style = style;
            this.length = end - start;
        }
    }

    @FXML
    private void initialize() {
        List<Button> colorButtons = Arrays.asList(
                blackButton, darkBlueButton, darkGreenButton, darkAquaButton,
                darkRedButton, darkPurpleButton, goldButton, grayButton,
                darkGrayButton, blueButton, greenButton, aquaButton,
                redButton, lightPurpleButton, yellowButton, whiteButton
        );
        colorButtons.forEach(this::setButtonClip);

        textCodeArea.textProperty().addListener((observable, oldValue, newValue) -> {
            applyCodesToPreview(newValue);
        });

        for (int i = 0; i < previewTextArea.getParagraphs().size(); i++) {
            previewTextArea.setParagraphStyle(i, "-fx-text-alignment: center;");
        }

        boldButton.setOnAction(e -> {
            toggleStyle("-fx-font-weight: bold;");
            toggleCode("l", "r");
        });
        italicButton.setOnAction(e -> {
            toggleStyle("-fx-font-style: italic;");
            toggleCode("o", "r");
        });
        underlineButton.setOnAction(e -> {
            toggleStyle("-fx-underline: true;");
            toggleCode("n", "r");
        });
        strikethroughButton.setOnAction(e -> {
            toggleStyle("-fx-strikethrough: true;");
            toggleCode("m", "r");
        });
        obfuscatedButton.setOnAction(e -> toggleCode("k", "r"));

        resetButton.setOnAction(e -> {
            resetStyle();
            toggleCode("r", "r");
        });

        blackButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #000000;");
            toggleCode("0", "r");
        });
        darkBlueButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #0000AA;");
            toggleCode("1", "r");
        });
        darkGreenButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #00AA00;");
            toggleCode("2", "r");
        });
        darkAquaButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #00AAAA;");
            toggleCode("3", "r");
        });
        darkRedButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #AA0000;");
            toggleCode("4", "r");
        });
        darkPurpleButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #AA00AA;");
            toggleCode("5", "r");
        });
        goldButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #FFAA00;");
            toggleCode("6", "r");
        });
        grayButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #AAAAAA;");
            toggleCode("7", "r");
        });
        darkGrayButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #555555;");
            toggleCode("8", "r");
        });
        blueButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #5555FF;");
            toggleCode("9", "r");
        });
        greenButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #55FF55;");
            toggleCode("a", "r");
        });
        aquaButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #55FFFF;");
            toggleCode("b", "r");
        });
        redButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #FF5555;");
            toggleCode("c", "r");
        });
        lightPurpleButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #FF55FF;");
            toggleCode("d", "r");
        });
        yellowButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #FFFF55;");
            toggleCode("e", "r");
        });
        whiteButton.setOnAction(e -> {
            toggleStyle("-fx-fill: #FFFFFF;");
            toggleCode("f", "r");
        });

        copyButton.setOnAction(e -> onCopyToClipboard());

        motdButton.setOnAction(e -> {
            isMOTDMode = true;
            updateModeButtonStyles();
            String currentText = textCodeArea.getText();
            if (!currentText.isEmpty()) {
                String updatedText = currentText.replace("§", "\\u00A7");
                textCodeArea.setText(updatedText);
            }
            applyCodesToPreview(textCodeArea.getText());
        });

        chatCodeButton.setOnAction(e -> {
            isMOTDMode = false;
            updateModeButtonStyles();
            String currentText = textCodeArea.getText();
            if (!currentText.isEmpty()) {
                String updatedText = currentText.replace("\\u00A7", "§");
                textCodeArea.setText(updatedText);
            }
            applyCodesToPreview(textCodeArea.getText());
        });

        updateModeButtonStyles();

        textCodeArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!previewTextArea.getText().equals(newValue)) {
                previewTextArea.replaceText(newValue);
            }
        });
    }

    private void updateModeButtonStyles() {
        if (isMOTDMode) {
            motdButton.setStyle("-fx-background-color: #40E0D0;");
            chatCodeButton.setStyle("");
        } else {
            chatCodeButton.setStyle("-fx-background-color: #40E0D0;");
            motdButton.setStyle("");
        }
    }

    private void setButtonClip(Button button) {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(8);
        clip.setArcHeight(8);
        button.setClip(clip);

        button.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            clip.setWidth(newBounds.getWidth());
            clip.setHeight(newBounds.getHeight());
        });
    }

    private void applyCodesToPreview(String codesText) {
        final String processedCodesText = isMOTDMode
                ? codesText.replaceAll("\\\\u00A7", "§")
                : codesText;

        Platform.runLater(() -> {
            if (obfuscationTimeline != null) {
                obfuscationTimeline.stop();
                obfuscationTimeline = null;
            }
            obfuscationSegments.clear();
            previewTextArea.clear();

            int index = 0;
            StringBuilder segmentBuffer = new StringBuilder();
            String colorStyle = "";
            String formatStyle = "";
            boolean currentObfuscate = false;

            Random random = new Random();
            String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

            while (index < processedCodesText.length()) {
                char c = processedCodesText.charAt(index);
                if (c == '§' && index + 1 < processedCodesText.length()) {
                    if (segmentBuffer.length() > 0) {
                        String segment = segmentBuffer.toString();
                        int segInsertStart = previewTextArea.getLength();
                        String currentStyle = colorStyle + formatStyle;
                        if (currentObfuscate) {
                            StringBuilder obfSegment = new StringBuilder();
                            for (int i = 0; i < segment.length(); i++) {
                                obfSegment.append(charSet.charAt(random.nextInt(charSet.length())));
                            }
                            previewTextArea.appendText(obfSegment.toString());
                            int segInsertEnd = previewTextArea.getLength();
                            previewTextArea.setStyle(segInsertStart, segInsertEnd, currentStyle);
                            obfuscationSegments.add(new ObfuscationSegment(segInsertStart, segInsertEnd, currentStyle));
                        } else {
                            previewTextArea.appendText(segment);
                            int segInsertEnd = previewTextArea.getLength();
                            previewTextArea.setStyle(segInsertStart, segInsertEnd, currentStyle);
                        }
                        segmentBuffer.setLength(0);
                    }
                    char code = processedCodesText.charAt(index + 1);
                    index += 2;
                    switch (code) {
                        case '0': colorStyle = "-fx-fill: #000000; "; break;
                        case '1': colorStyle = "-fx-fill: #0000AA; "; break;
                        case '2': colorStyle = "-fx-fill: #00AA00; "; break;
                        case '3': colorStyle = "-fx-fill: #00AAAA; "; break;
                        case '4': colorStyle = "-fx-fill: #AA0000; "; break;
                        case '5': colorStyle = "-fx-fill: #AA00AA; "; break;
                        case '6': colorStyle = "-fx-fill: #FFAA00; "; break;
                        case '7': colorStyle = "-fx-fill: #AAAAAA; "; break;
                        case '8': colorStyle = "-fx-fill: #555555; "; break;
                        case '9': colorStyle = "-fx-fill: #5555FF; "; break;
                        case 'a': colorStyle = "-fx-fill: #55FF55; "; break;
                        case 'b': colorStyle = "-fx-fill: #55FFFF; "; break;
                        case 'c': colorStyle = "-fx-fill: #FF5555; "; break;
                        case 'd': colorStyle = "-fx-fill: #FF55FF; "; break;
                        case 'e': colorStyle = "-fx-fill: #FFFF55; "; break;
                        case 'f': colorStyle = "-fx-fill: #FFFFFF; "; break;
                        case 'l': formatStyle += "-fx-font-weight: bold; "; break;
                        case 'o': formatStyle += "-fx-font-style: italic; "; break;
                        case 'n': formatStyle += "-fx-underline: true; "; break;
                        case 'm': formatStyle += "-fx-strikethrough: true; "; break;
                        case 'k': currentObfuscate = true; break;
                        case 'r':
                            colorStyle = "";
                            formatStyle = "";
                            currentObfuscate = false;
                            break;
                        default:
                            segmentBuffer.append("§").append(code);
                            break;
                    }
                } else {
                    segmentBuffer.append(c);
                    index++;
                }
            }
            if (segmentBuffer.length() > 0) {
                String segment = segmentBuffer.toString();
                int segInsertStart = previewTextArea.getLength();
                String currentStyle = colorStyle + formatStyle;
                if (currentObfuscate) {
                    StringBuilder obfSegment = new StringBuilder();
                    for (int i = 0; i < segment.length(); i++) {
                        obfSegment.append(charSet.charAt(random.nextInt(charSet.length())));
                    }
                    previewTextArea.appendText(obfSegment.toString());
                    int segInsertEnd = previewTextArea.getLength();
                    previewTextArea.setStyle(segInsertStart, segInsertEnd, currentStyle);
                    obfuscationSegments.add(new ObfuscationSegment(segInsertStart, segInsertEnd, currentStyle));
                } else {
                    previewTextArea.appendText(segment);
                    int segInsertEnd = previewTextArea.getLength();
                    previewTextArea.setStyle(segInsertStart, segInsertEnd, currentStyle);
                }
            }
            if (!obfuscationSegments.isEmpty()) {
                obfuscationTimeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
                    for (ObfuscationSegment seg : obfuscationSegments) {
                        StringBuilder newObf = new StringBuilder();
                        for (int i = 0; i < seg.length; i++) {
                            newObf.append(charSet.charAt(random.nextInt(charSet.length())));
                        }
                        previewTextArea.replaceText(seg.start, seg.start + seg.length, newObf.toString());
                        previewTextArea.setStyle(seg.start, seg.start + seg.length, seg.style);
                    }
                }));
                obfuscationTimeline.setCycleCount(Timeline.INDEFINITE);
                obfuscationTimeline.play();
            }
        });
    }

    @FXML
    private void toggleStyle(String newStyle) {
        int start = previewTextArea.getSelection().getStart();
        int end = previewTextArea.getSelection().getEnd();
        if (start == end) {
            return;
        }
        String currentStyle = previewTextArea.getStyleAtPosition(start);
        if (currentStyle == null || !currentStyle.contains(newStyle)) {
            String combinedStyle = (currentStyle == null ? "" : currentStyle + " ") + newStyle;
            previewTextArea.setStyle(start, end, combinedStyle.trim());
        }
    }

    private void toggleCode(String code, String endCode) {
        String prefix = isMOTDMode ? "\\u00A7" : "§";
        String startCode = prefix + code;
        String fullEndCode = prefix + endCode;

        int selStart = textCodeArea.getSelection().getStart();
        int selEnd = textCodeArea.getSelection().getEnd();
        String fullText = textCodeArea.getText();

        boolean isColorCode = code.matches("[0-9a-f]");

        String before = fullText.substring(0, selStart);
        String selected = fullText.substring(selStart, selEnd);
        String after = fullText.substring(selEnd);

        if (selStart == selEnd) {
            textCodeArea.setText(before + startCode + after);
            return;
        }

        if (isColorCode) {
            int expandedStart = selStart;
            while (expandedStart >= 2) {
                String possibleCode = fullText.substring(expandedStart - 2, expandedStart);
                if ((!isMOTDMode && possibleCode.matches("§[0-9a-fk-or]")) ||
                        (isMOTDMode && possibleCode.matches("\\\\u00A7[0-9a-fk-or]"))) {
                    expandedStart -= 2;
                } else {
                    break;
                }
            }
            before = fullText.substring(0, expandedStart);
            selected = fullText.substring(expandedStart, selEnd);

            Pattern p = Pattern.compile(isMOTDMode ? "^(\\\\u00A7[0-9a-fk-or]+)" : "^(§[0-9a-fk-or]+)");
            Matcher m = p.matcher(selected);
            String prefixCodes = "";
            if (m.find()) {
                prefixCodes = m.group();
            }
            String withoutColor = isMOTDMode
                    ? prefixCodes.replaceAll("\\\\u00A7[0-9a-f]", "")
                    : prefixCodes.replaceAll("§[0-9a-f]", "");
            String newPrefix = startCode + withoutColor;
            if (selected.startsWith(prefixCodes)) {
                selected = selected.substring(prefixCodes.length());
            }
            selected = newPrefix + selected;
            if (isMOTDMode) {
                selected = selected.replaceAll("(\\\\u00A7r)+$", "");
                after = after.replaceAll("^(\\\\u00A7r)+", "");
            } else {
                selected = selected.replaceAll("(§r)+$", "");
                after = after.replaceAll("^(§r)+", "");
            }
            String newText = before + selected + fullEndCode + after;
            if (isMOTDMode) {
                newText = newText.replaceAll("(\\\\u00A7r){2,}", "\\\\u00A7r");
            } else {
                newText = newText.replaceAll("(§r){2,}", "§r");
            }
            textCodeArea.setText(newText);
            return;
        }

        int codeLength = isMOTDMode ? 7 : 2;
        String codeRegex = isMOTDMode ? "(\\\\u00A7.)" : "(§.)";
        Pattern prefixPattern = Pattern.compile("^(" + codeRegex + ")+");
        Matcher prefixMatcher = prefixPattern.matcher(selected);
        String prefixCodes = "";
        if (prefixMatcher.find()) {
            prefixCodes = prefixMatcher.group();
        }
        StringBuilder colorPrefix = new StringBuilder();
        StringBuilder formatPrefix = new StringBuilder();
        for (int i = 0; i < prefixCodes.length(); i += codeLength) {
            String singleCode = prefixCodes.substring(i, i + codeLength);
            char codeChar = isMOTDMode ? singleCode.charAt(codeLength - 1) : singleCode.charAt(1);
            if (codeChar == 'r') continue;
            if ((codeChar >= '0' && codeChar <= '9') ||
                    (codeChar >= 'a' && codeChar <= 'f') ||
                    (codeChar >= 'A' && codeChar <= 'F')) {
                colorPrefix.append(singleCode);
            } else {
                formatPrefix.append(singleCode);
            }
        }
        String newPrefix = colorPrefix.toString() + startCode + formatPrefix.toString();
        selected = newPrefix + selected.substring(prefixCodes.length());

        if (isMOTDMode) {
            selected = selected.replaceAll("(\\\\u00A7r)+$", "");
            after = after.replaceAll("^(\\\\u00A7r)+", "");
        } else {
            selected = selected.replaceAll("(§r)+$", "");
            after = after.replaceAll("^(§r)+", "");
        }
        String newText = before + selected + fullEndCode + after;
        if (isMOTDMode) {
            newText = newText.replaceAll("(\\\\u00A7r){2,}", "\\\\u00A7r");
        } else {
            newText = newText.replaceAll("(§r){2,}", "§r");
        }
        textCodeArea.setText(newText);
    }

    @FXML
    private void resetStyle() {
        int start = previewTextArea.getSelection().getStart();
        int end = previewTextArea.getSelection().getEnd();
        if (start == end) {
            return;
        }
        previewTextArea.clearStyle(start, end);
    }

    @FXML
    public void onCopyToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(textCodeArea.getText());
        clipboard.setContent(content);
        showPopup("Text copied!");
    }

    private void showPopup(String message) {
        Popup popup = new Popup();

        Label popupLabel = new Label(message);
        popupLabel.getStyleClass().add("popup-message");

        StackPane root = new StackPane(popupLabel);
        Scene popupScene = new Scene(root);
        popupScene.setFill(null);

        if (copyButton.getScene() != null && !copyButton.getScene().getStylesheets().isEmpty()) {
            popupScene.getStylesheets().addAll(copyButton.getScene().getStylesheets());
        }

        popup.getContent().add(root);
        popup.setAutoHide(true);

        Window window = copyButton.getScene().getWindow();
        popup.show(window);

        Platform.runLater(() -> {
            Bounds bounds = copyButton.localToScreen(copyButton.getBoundsInLocal());
            double popupWidth = popup.getWidth();
            double popupHeight = popup.getHeight();
            double x = bounds.getMinX() + (bounds.getWidth() - popupWidth) / 2;
            double y = bounds.getMinY() - popupHeight - 5;
            popup.setX(x);
            popup.setY(y);
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> popup.hide()));
        timeline.play();
    }
}