package com.tops.demo.lucene.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LatLonDocValuesField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tops.demo.lucene.model.Test;

/**
 * build index
 * @author ethan
 *
 */
@Service
public class IndexWriteService {
	
	@Value("${lucene.index.path}")
	private String indexPath;
	
	public void createIndex(boolean pCreate) {
		try {
			Directory lvDir = FSDirectory.open(Paths.get(indexPath));
			IndexWriterConfig lvIwc = new IndexWriterConfig(new StandardAnalyzer());
			
			if (pCreate) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				lvIwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				lvIwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			
			IndexWriter lvWriter = new IndexWriter(lvDir, lvIwc);
			indexDocs(lvWriter);
			
			lvWriter.forceMerge(1);
			lvWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Indexes documents
	 * @param pWriter
	 * @throws IOException 
	 */
	private void indexDocs(IndexWriter pWriter) throws IOException {
		List<Test> lvArticles = this.findDataFromDatabase();
		
		for (Test lvArticle : lvArticles) {
			Document lvDocument = new Document();

			lvDocument.add(new StringField("title", lvArticle.getTitle(), Store.YES));
			lvDocument.add(new StringField("author", lvArticle.getAuthor(), Store.YES));
			lvDocument.add(new TextField("content", lvArticle.getContent(), Store.YES)); // TextField会做分词处理
			lvDocument.add(new StoredField("sequence", lvArticle.getSequence())); 
			lvDocument.add(new NumericDocValuesField("sequence", lvArticle.getSequence())); // 排序字段
			lvDocument.add(new LatLonDocValuesField("coord", lvArticle.getLat(), lvArticle.getLon()));
			lvDocument.add(new StoredField("lat", lvArticle.getLat()));
			lvDocument.add(new StoredField("lon", lvArticle.getLon()));
			
			pWriter.addDocument(lvDocument);
		}
	}
	
	private List<Test> findDataFromDatabase() {
		List<Test> lvArticles = new ArrayList<>(); 
		Test lvArticle = new Test();
		lvArticle.setAuthor("张三");
		lvArticle.setTitle("Lucene是拿来干什么的？");
		lvArticle.setContent("Lucene是apache软件基金会4 jakarta项目组的一个子项目，是一个开放源代码的全文检索引擎工具包，但它不是一个完整的全文检索引擎，而是一个全文检索引擎的架构，提供了完整的查询引擎和索引引擎，部分文本分析引擎（英文与德文两种西方语言）。");
		lvArticle.setSequence(100);
		lvArticle.setLat(31.345512);
		lvArticle.setLon(121.438357);
		lvArticles.add(lvArticle);

		Test lvArticle1 = new Test();
		lvArticle1.setAuthor("李四");
		lvArticle1.setTitle("版本 发布日期 里程碑");
		lvArticle1.setContent("Lucene最初是由Doug Cutting开发的，在SourceForge的网站上提供下Lucene 图片Lucene 图片(2张)载。在2001年9月做为高质量的开源Java产品加入到Apache软件基金会的 Jakarta家族中。随着每个版本的发布，这个项目得到明显的增强，也吸引了更多的用户和开发人员。");
		lvArticle1.setSequence(10);
		lvArticle1.setLat(31.156873);
		lvArticle1.setLon(121.81484);
		lvArticles.add(lvArticle1);

		Test lvArticle2 = new Test();
		lvArticle2.setAuthor("王五");
		lvArticle2.setTitle("版本 发布日期 里程碑哈哈哈");
		lvArticle2.setContent("Lucene最初是由Doug Cutting开发的，在SourceForge的网站上提供下Lucene 图片Lucene 图片(2张)载。在2001年9月做为高质量的开源Java产品加入到Apache软件基金会的 Jakarta家族中。随着每个版本的发布，这个项目得到明显的增强，也吸引了更多的用户和开发人员。");
		lvArticle2.setSequence(1000);
		lvArticle2.setLat(28.153196);
		lvArticle2.setLon(113.075067);
		lvArticles.add(lvArticle2);
		
		return lvArticles;
	}
	
}
