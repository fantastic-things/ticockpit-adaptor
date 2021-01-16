package com.strrl.ticockpitadaptor;

import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * <p>
 *
 * @author strrl
 * @date 2021/1/16 20:08
 */
@RestController
@RequestMapping("/")
public class QueueController {
    public static final int QUEUE_CAP = 10000;
    private final Queue<String> q;

    public QueueController() {
        this.q = new ConcurrentLinkedDeque<>();
    }

    @GetMapping(value = "/queue/get/text", produces = "application/text")
    public String fetchText() {
        final int size = this.q.size();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            final String line = this.q.poll();
            if (line == null) {
                break;
            }
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

    @GetMapping(value = "/queue/get/json", produces = "application/json")
    public List<Line> fetchJson() {
        final int size = this.q.size();
        final List<Line> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final String line = this.q.poll();
            if (line == null) {
                break;
            }
            result.add(new Line(i, line));
        }
        return result;

    }

    @PutMapping("/queue/put")
    public int put(@RequestBody(required = false) String element) {
        if (element == null || element.trim().length() == 0) {
            return 0;
        }
        if (this.q.size() >= QUEUE_CAP) {
            return 0;
        }
        final Scanner scanner = new Scanner(new ByteArrayInputStream(element.getBytes()));
        int result = 0;
        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            this.q.add(line);
            result += 1;
        }
        return result;
    }

    public static final class Line {
        private int line;
        private String content;

        public Line(int line, String content) {
            this.line = line;
            this.content = content;
        }

        public int getLine() {
            return this.line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }

            Line line1 = (Line) o;

            if (this.line != line1.line) {
                return false;
            }
            return this.content != null ? this.content.equals(line1.content) : line1.content == null;
        }

        @Override
        public int hashCode() {
            int result = this.line;
            result = 31 * result + (this.content != null ? this.content.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Line{" +
                    "line=" + line +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}
