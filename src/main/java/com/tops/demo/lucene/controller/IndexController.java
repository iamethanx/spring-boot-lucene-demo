package com.tops.demo.lucene.controller;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tops.demo.lucene.model.TestQo;
import com.tops.demo.lucene.service.IndexSearcherService;
import com.tops.demo.lucene.service.IndexWriteService;

/**
 * 
 * @author ethan
 *
 */
@RestController
public class IndexController {
	
	@Autowired
	private IndexSearcherService indexSearcherService;
	@Autowired
	private IndexWriteService indexWriteService;

	@RequestMapping("/")
	public Object index() {
		return "OK";
	}
	
	@RequestMapping("/createIndex")
	public Object createIndex() {
		indexWriteService.createIndex(true);
		return "OK";
	}
	
	@RequestMapping("/search")
	public Object search(TestQo pQo) throws IOException, ParseException {
		return indexSearcherService.search(pQo);
	}
	
}
