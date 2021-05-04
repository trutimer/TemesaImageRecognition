# Temesa Car type Recognition

## Overview

This is Application for car type recognition at Temesa.

## Assets folder

_Do not delete the assets folder content_. If you explicitly deleted the files,
choose `Build -> Rebuild` to re-download the deleted model files into the assets
folder.


## Download

Grab the latest AAR via Gradle:
```groovy
implementation 'com.github.trutimer:TemesaImageRecognition:1.0.7'
```
or Maven:
```xml
<dependency>
        <groupId>com.github.trutimer</groupId>
        <artifactId>TemesaImageRecognition</artifactId>
        <version>1.0.7</version>
</dependency>
```

## How to use

```java
// To get image category just pass the bitmap as follows:

ProcessImage imageProcess = new ProcessImage(activity);
            imageProcess.getImageResults(results -> {

                Classifier.Recognition recognition = results.get(0);
                if (recognition != null) {
                    if (recognition.getTitle() != null) activity.txtResults.setText("TITLE: "+recognition.getTitle());
                    if (recognition.getId() != null) activity.txtResults.append("\nID: "+recognition.getId());
                    if (recognition.getConfidence() != null) activity.txtResults.append("\nCONFIDENCE: "+String.format("%.2f", (100 * recognition.getConfidence())) + "%");
                }
            });
imageProcess.processImage(bitmap);
```
