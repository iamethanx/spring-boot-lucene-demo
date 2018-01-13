package com.tops.demo.lucene.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tops.demo.lucene.model.Test;
import com.tops.demo.lucene.model.TestQo;

/**
 * search
 * @author ethan
 *
 */
@Service
public class IndexSearcherService {

	@Value("${lucene.index.path}")
	private String indexPath;
	
	/**
	 * 搜索
	 * 实际环境中IndexSearcher需要做成单例
	 * @param pQo
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<Test> search(TestQo pQo) throws IOException, ParseException {
		
		List<Test> lvResults = new ArrayList<Test>();
		
		IndexReader lvReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		IndexSearcher lvSearcher = new IndexSearcher(lvReader);;
		
		// sort
		TopDocs lvDocs = lvSearcher.search(this.createQuery(pQo), 100, this.createSort());
		int lvTotalHits = Math.toIntExact(lvDocs.totalHits);
		for (int i = 0; i < lvTotalHits; i++) {
			
			Document doc = lvSearcher.doc(lvDocs.scoreDocs[i].doc);
			
			Test lvArticle = new Test();
			lvArticle.setTitle(doc.get("title"));
			lvArticle.setAuthor(doc.get("author"));
			lvArticle.setContent(doc.get("content"));
			lvArticle.setSequence(Integer.parseInt(doc.get("sequence")));
			
			lvResults.add(lvArticle);
		}
		
		return lvResults;
	}
	
	/**
	 * Build BooleanQuery
	 * @param pParamMap
	 * @return
	 * @throws ParseException
	 */
	private Query createQuery(TestQo pQo)
			throws ParseException {
		
		BooleanQuery.Builder lvBuilder = new BooleanQuery.Builder();
		 
		if (StringUtils.isNotBlank(pQo.getKw())) {
			QueryParser lvQueryParser = new QueryParser("content", new StandardAnalyzer()); // 分词 如软件基金会可能分为 软件|基金会两个词搜索
			lvQueryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
			lvBuilder.add(new BooleanClause(lvQueryParser.parse(pQo.getKw()), BooleanClause.Occur.MUST));
		}
		
		if (StringUtils.isNotBlank(pQo.getAuthor())) {
			// TermQuery不分词全匹配搜索
			lvBuilder.add(new BooleanClause(new TermQuery(new Term("author", pQo.getAuthor())), BooleanClause.Occur.MUST));
		}
		
		return lvBuilder.build();
	}
	
	private Sort createSort() {
		return new Sort(new SortField("sequence", Type.INT, false)); // true从大到小 false从小到大
	}
	
}
