package com.example.demo.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDNumberTreeNode;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationService {

    public byte[] generateTaggedPdf() {
        try (PDDocument document = new PDDocument()) {
            // Document Title
            String title = "Accessible PDF Report";

            // 1. Setup Document Catalog and Metadata
            PDPage page = new PDPage();
            document.addPage(page);

            document.getDocumentInformation().setTitle(title);

            // 2. Setup MarkInfo (Required for Tagged PDF)
            PDMarkInfo markInfo = new PDMarkInfo();
            markInfo.setMarked(true);
            document.getDocumentCatalog().setMarkInfo(markInfo);

            // 3. Setup Structure Tree
            PDStructureTreeRoot treeRoot = new PDStructureTreeRoot();
            document.getDocumentCatalog().setStructureTreeRoot(treeRoot);

            // Create Root Element (Document)
            PDStructureElement documentStructure = new PDStructureElement(StandardStructureTypes.DOCUMENT, treeRoot);
            treeRoot.appendKid(documentStructure);

            // 4. Create Content and Structure Elements
            // Using Standard 14 Fonts (Helvetica) for simplicity.
            // Note: For full PDF/UA compliance, fonts should be embedded (using TTF).
            // This sample focuses on the Tagging structure.
            var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            var bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            PDStructureElement h1 = null;
            PDStructureElement p = null;
            PDStructureElement p2 = null;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // --- Section 1: H1 Title ---
                int mcidH1 = 1;
                contentStream.beginText();
                contentStream.setFont(font, 18);
                contentStream.newLineAtOffset(50, 700);

                // Start Marked Content Sequence
                COSDictionary h1Props = new COSDictionary();
                h1Props.setInt(COSName.MCID, mcidH1);
                contentStream.beginMarkedContent(COSName.P, PDPropertyList.create(h1Props));

                contentStream.showText(title);
                contentStream.endMarkedContent();
                contentStream.endText();

                // Create Structure Element for H1
                h1 = new PDStructureElement(StandardStructureTypes.H1, documentStructure);
                h1.setPage(page);
                // Link structure to content via MCID
                COSDictionary h1Dict = h1.getCOSObject();
                h1Dict.setInt(COSName.MCID, mcidH1);
                documentStructure.appendKid(h1);


                // --- Section 2: Paragraph ---
                int mcidP = 2;
                contentStream.beginText();
                contentStream.setFont(bodyFont, 12);
                contentStream.newLineAtOffset(50, 650);

                COSDictionary pProps = new COSDictionary();
                pProps.setInt(COSName.MCID, mcidP);
                contentStream.beginMarkedContent(COSName.P, PDPropertyList.create(pProps));

                contentStream.showText("This is a sample paragraph generated with Apache PDFBox.");
                contentStream.endMarkedContent();
                contentStream.endText();

                // Create Structure Element for P
                p = new PDStructureElement(StandardStructureTypes.P, documentStructure);
                p.setPage(page);
                COSDictionary pDict = p.getCOSObject();
                pDict.setInt(COSName.MCID, mcidP);
                documentStructure.appendKid(p);


                // --- Section 3: Another Paragraph (Simulating Table or List would be more complex) ---
                int mcidP2 = 3;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 630);

                COSDictionary p2Props = new COSDictionary();
                p2Props.setInt(COSName.MCID, mcidP2);
                contentStream.beginMarkedContent(COSName.P, PDPropertyList.create(p2Props));

                contentStream.showText("Tagging ensures screen readers can interpret the reading order.");
                contentStream.endMarkedContent();
                contentStream.endText();

                p2 = new PDStructureElement(StandardStructureTypes.P, documentStructure);
                p2.setPage(page);
                COSDictionary p2Dict = p2.getCOSObject();
                p2Dict.setInt(COSName.MCID, mcidP2);
                documentStructure.appendKid(p2);
            }

            // Note: ParentTree is required for bidirectional lookup (Structure -> Page content)
            // It maps Page Index -> Array of Parent Structure Elements for MCIDs on that page
            // Or maps MCID directly if global?
            // Actually, PDFBox handles some of this if we construct it correctly, but usually we need to set the ParentTree.
            // Let's create a simple NumberTree for ParentTree if PDFBox doesn't auto-handle it fully in 3.0.
            // In 3.0, it is often manual.

            // Constructing ParentTree (Number Tree) mapping MCIDs to Structure Elements
            // The key in ParentTree is a global index?
            // Actually, for simple cases, the StructureElement.setPage(page) and linking MCID is the forward pointer.
            // The Page object needs a StructParents entry.
            page.getCOSObject().setInt(COSName.STRUCT_PARENTS, 0);

            // The ParentTree in StructTreeRoot maps the StructParents index (0) to the array of structure elements
            // corresponding to MCIDs on that page.
            // MCID 1 -> h1
            // MCID 2 -> p
            // MCID 3 -> p2

            // This part is tricky in raw PDFBox.
            // Let's define the ParentTreeNextKey and the tree itself.
            COSDictionary parentTreeDict = new COSDictionary();
            COSArray nums = new COSArray();

            // Entry 0 (corresponding to page StructParents=0)
            COSArray page0Parents = new COSArray();
            // This array should contain the Structure Elements for MCID 0, MCID 1, MCID 2...
            // But wait, MCIDs are 1, 2, 3.
            // So index 1 of this array = h1?
            // No, the mapping is usually: The value at key 'k' in NumberTree is an array (or dictionary)
            // containing the parent objects for the MCIDs on the page.
            // If the value is an array, the index in the array matches the MCID.
            // So if MCIDs are 1, 2, 3, we need an array of size 4 (index 0 unused/null or whatever).

            page0Parents.add(null); // MCID 0 unused
            page0Parents.add(h1);   // MCID 1
            page0Parents.add(p);    // MCID 2
            page0Parents.add(p2);   // MCID 3

            nums.add(org.apache.pdfbox.cos.COSInteger.get(0)); // Key: 0 (StructParents index)
            nums.add(page0Parents); // Value: The array of parents

            parentTreeDict.setItem(COSName.NUMS, nums);
            treeRoot.setParentTree(new PDNumberTreeNode(parentTreeDict, COSArray.class));
            treeRoot.setParentTreeNextKey(1);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Failed to generate PDF", e);
            throw new RuntimeException("PDF Generation failed", e);
        }
    }
}
