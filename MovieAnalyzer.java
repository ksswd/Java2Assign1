import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieAnalyzer {
    String data;

    public MovieAnalyzer(String data) {
        this.data = data;
    }

    public Map<Integer, Integer> getMovieCountByYear() throws IOException {
        Stream<String> stream = Files.lines(Paths.get(data), StandardCharsets.UTF_8);
        Map<Integer, Integer> map = new HashMap<>();
        stream.filter(s -> {
            String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            return !Objects.equals(strings[0], "Poster_Link");
        }).forEach(s -> {
            String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            int year = Integer.parseInt(strings[2]);
            map.putIfAbsent(year, 0);
            map.replace(year, map.get(year) + 1);
        });
        return map.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    public Map<String, Integer> getMovieCountByGenre() throws IOException {
        Stream<String> stream = Files.lines(Paths.get(data), StandardCharsets.UTF_8);
        Map<String, Integer> map = new HashMap<>();
        stream.filter(s -> {
            String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            return !Objects.equals(strings[0], "Poster_Link");
        }).forEach(s -> {
            String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            String[] Genre = strings[5].replace("\"", "").replace(" ", "").split(",");
            for (String value : Genre) {
                map.putIfAbsent(value, 0);
                map.replace(value, map.get(value) + 1);
            }
        });
        return map.entrySet()
                .stream()
                .sorted((e1, e2) -> {
                    if (e1.getValue() == e2.getValue()) {
                        return e1.getKey().compareTo(e2.getKey());
                    } else {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Map<List<String>, Integer> getCoStarCount() throws IOException {
        Stream<String> stream = Files.lines(Paths.get(data), StandardCharsets.UTF_8);
        Map<List<String>, Integer> map = new HashMap<>();
        stream.filter(s -> {
            String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            return !Objects.equals(strings[0], "Poster_Link");
        }).forEach(s -> {
            String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            String[] stars = {strings[10], strings[11], strings[12], strings[13]};
            for (int i = 0; i < 4; i++) {
                for (int j = i + 1; j < 4; j++) {
                    List<String> cor = new ArrayList<>();
                    cor.add(stars[i]);
                    cor.add(stars[j]);
                    Collections.sort(cor);
                    map.putIfAbsent(cor, 0);
                    map.replace(cor, map.get(cor) + 1);
                }
            }
        });
        return map;
    }

    public List<String> getTopMovies(int top_k, String by) throws IOException {
        Stream<String> stream = Files.lines(Paths.get(data), StandardCharsets.UTF_8);
        List<String> ans = new ArrayList<>();
        List<Integer> time = new ArrayList<>();
        Map<Integer, List<String>> map = new HashMap<>();
        if (Objects.equals(by, "runtime")) {
            stream.filter(s -> {
                String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                return !Objects.equals(strings[0], "Poster_Link");
            }).forEach(s -> {
                String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                int t = Integer.parseInt(strings[4].replace(" min", ""));
                if (time.size() < top_k && !time.contains(t)) {
                    time.add(t);
                    Collections.sort(time);
                    map.putIfAbsent(t, new ArrayList<>());
                    if (!map.get(t).contains(strings[1].replace("\"", ""))) {
                        map.get(t).add(strings[1].replace("\"", ""));
                    }
                } else if (t > time.get(0) && !time.contains(t)) {
                    time.remove(0);
                    time.add(t);
                    Collections.sort(time);
                    map.putIfAbsent(t, new ArrayList<>());
                    if (!map.get(t).contains(strings[1].replace("\"", ""))) {
                        map.get(t).add(strings[1].replace("\"", ""));
                    }
                } else if (time.contains(t)) {
                    if (!map.get(t).contains(strings[1].replace("\"", ""))) {
                        map.get(t).add(strings[1].replace("\"", ""));
                    }
                }
            });
            for (int i = top_k - 1; i >= 0; i--) {
                Collections.sort(map.get(time.get(i)));
                for (int j = 0; j < map.get(time.get(i)).size(); j++) {
                    ans.add(map.get(time.get(i)).get(j));
                    if (ans.size() == top_k) {
                        break;
                    }
                }
                if (ans.size() == top_k) {
                    break;
                }
            }
        } else {
            stream.filter(s -> {
                String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                return !Objects.equals(strings[0], "Poster_Link");
            }).forEach(s -> {
                String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                int t = strings[7].length();
                if (strings[7].charAt(0) == '\"') {
                    t -= 2;
                }
                if (time.size() < top_k && !time.contains(t)) {
                    time.add(t);
                    Collections.sort(time);
                    map.putIfAbsent(t, new ArrayList<>());
                    if (!map.get(t).contains(strings[1].replace("\"", ""))) {
                        map.get(t).add(strings[1].replace("\"", ""));
                    }
                } else if (t > time.get(0) && !time.contains(t)) {
                    time.remove(0);
                    time.add(t);
                    Collections.sort(time);
                    map.putIfAbsent(t, new ArrayList<>());
                    if (!map.get(t).contains(strings[1].replace("\"", ""))) {
                        map.get(t).add(strings[1].replace("\"", ""));
                    }
                } else if (time.contains(t)) {
                    if (!map.get(t).contains(strings[1].replace("\"", ""))) {
                        map.get(t).add(strings[1].replace("\"", ""));
                    }
                }
            });
            for (int i = top_k - 1; i >= 0; i--) {
                Collections.sort(map.get(time.get(i)));
                for (int j = 0; j < map.get(time.get(i)).size(); j++) {
                    ans.add(map.get(time.get(i)).get(j));
                    if (ans.size() == top_k) {
                        break;
                    }
                }
                if (ans.size() == top_k) {
                    break;
                }
            }
        }
        return ans;
    }

    public List<String> getTopStars(int top_k, String by) throws IOException {
        Stream<String> stream = Files.lines(Paths.get(data), StandardCharsets.UTF_8);
        List<String> ans = new ArrayList<>();
        List<String> stars = new ArrayList<>();
        if (Objects.equals(by, "rating")) {
            Map<String, Double> map1 = new HashMap<>();
            Map<String, Integer> map2 = new HashMap<>();
            Map<String, Double> map3 = new HashMap<>();
            stream.filter(s -> {
                String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                return !Objects.equals(strings[0], "Poster_Link");
            }).forEach(s -> {
                String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                Float point = Float.parseFloat(strings[6]);
                for (int i = 10; i < 14; i++) {
                    if (!stars.contains(strings[i])) {
                        stars.add(strings[i]);
                    }
                    map1.putIfAbsent(strings[i], 0d);
                    map2.putIfAbsent(strings[i], 0);
                    map1.replace(strings[i], map1.get(strings[i]) + point);
                    map2.replace(strings[i], map2.get(strings[i]) + 1);
                }
            });
            for (String star : stars) {
                map3.putIfAbsent(star, (map1.get(star) / map2.get(star)));
            }
            map3 = map3.entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        if (Objects.equals(String.valueOf(e1.getValue()),
                                String.valueOf(e2.getValue()))) {
                            return e1.getKey().compareTo(e2.getKey());
                        } else {
                            return e2.getValue().compareTo(e1.getValue());
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            for (String key : map3.keySet()) {
                ans.add(key);
                if (ans.size() == top_k) {
                    break;
                }
            }
        } else {
            Map<String, Long> map1 = new HashMap<>();
            Map<String, Integer> map2 = new HashMap<>();
            Map<String, Long> map3 = new HashMap<>();
            stream.filter(s -> {
                String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                return !Objects.equals(strings[0], "Poster_Link");
            }).forEach(s -> {
                String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (strings.length == 16) {
                    Long gross = Long.parseLong(strings[15].replace("\"", "").replace(",", ""));
                    for (int i = 10; i < 14; i++) {
                        if (!stars.contains(strings[i])) {
                            stars.add(strings[i]);
                        }
                        map1.putIfAbsent(strings[i], 0L);
                        map2.putIfAbsent(strings[i], 0);
                        map1.replace(strings[i], map1.get(strings[i]) + gross);
                        map2.replace(strings[i], map2.get(strings[i]) + 1);
                    }
                }
            });
            for (String star : stars) {
                map3.putIfAbsent(star, map1.get(star) / map2.get(star));
            }
            map3 = map3.entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        if (Objects.equals(String.valueOf(e1.getValue()),
                                String.valueOf(e2.getValue()))) {
                            return e1.getKey().compareTo(e2.getKey());
                        } else {
                            return e2.getValue().compareTo(e1.getValue());
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            for (String key : map3.keySet()) {
                ans.add(key);
                if (ans.size() == top_k) {
                    break;
                }
            }
        }
        return ans;
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime)
            throws IOException {
        Stream<String> stream = Files.lines(Paths.get(data), StandardCharsets.UTF_8);
        List<String> ans = new ArrayList<>();
        stream.filter(s -> {
            String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            return !Objects.equals(strings[0], "Poster_Link");
        }).forEach(s -> {
            String[] strings = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            List<String> Genre = List.of(strings[5].replace("\"", "")
                    .replace(" ", "").split(","));
            float point = Float.parseFloat(strings[6]);
            int t = Integer.parseInt(strings[4].replace(" min", ""));
            if (Genre.contains(genre) && point >= min_rating && t <= max_runtime) {
                ans.add(strings[1].replace("\"", ""));
            }
        });
        Collections.sort(ans);
        return ans;
    }
}
