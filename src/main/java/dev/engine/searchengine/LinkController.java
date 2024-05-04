package dev.engine.searchengine;

import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/queryquest/")
public class LinkController {
    private final LinkRepository linkRepository;
    public LinkController(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }
    @GetMapping("{query}")
    List<Link> search(@PathVariable String query) {
        return linkRepository.search(query);
    }

}
