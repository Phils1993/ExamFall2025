package app.services;

// FIXME: Denne her klasse indeholder kode som kan hjælpe dig med at hente fra et andet API
// Husk at sætte din API_KEY i miljø variablerne
// Jeg kan finde den herinde: https://www.themoviedb.org/settings/api


public class ServiceAPI {
    /*

    // todo ret sandsynligt at skulle bruge dette:
     private static final String BASE_URL = "https://packingapi.cphbusinessapps.dk/packinglist/";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;

    public PackingListService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<PackingItemDTO> getPackingList(String category) {
        HttpResponse<String> response;
        try {
            String url = BASE_URL + category.toLowerCase();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ItemListDTO itemList = objectMapper.readValue(response.body(), ItemListDTO.class);

                return itemList.getItems().stream()
                        .map(i -> new PackingItemDTO(
                                i.getName(),
                                i.getWeightInGrams() / 1000.0 // convert grams → kg
                        ))
                        .toList();
            } else {
                throw new ApiException(response.statusCode(), "Failed to fetch packing list: " + response.statusCode());
            }
         } catch (ApiException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ApiException(500,"Error fetching packing list");
        }
    }


    ------------------------------------------------------------------------------------------

    private static final String DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String MOVIE_BY_ID_URL = "https://api.themoviedb.org/3/movie/";

    private final ObjectMapper objectMapper;
    private final HttpClient client;
    private final String apiKey;
    int cores = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executor;

    public MovieService() {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.client = HttpClient.newHttpClient();
        this.apiKey = System.getenv("API_KEY");
        if (apiKey == null) {
            throw new RuntimeException("API_KEY not found in environment variables!");
        }
        this.executor = Executors.newFixedThreadPool(cores);
    }

    public List<MovieDetailsDTO> getDanishMoviesLast5Years() {
        List<MovieDetailsDTO> movies = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate fiveYearsAgo = today.minusYears(5);

        int page = 1;
        boolean morePages = true;

        while (morePages) {
            try {
                String url = DISCOVER_URL + "?api_key=" + apiKey + "&language=da-DK" + "&region=DK" +
                        "&with_origin_country=DK" +
                        "&primary_release_date.gte=" +
                        fiveYearsAgo +
                        "&primary_release_date.lte=" +
                        today +
                        "&sort_by=primary_release_date.desc" +
                        "&page=" +
                        page;


                HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 && response.body() != null) {
                    MovieResponse movieResponse = objectMapper.readValue(response.body(), MovieResponse.class);
                    if (movieResponse.getResults() != null && !movieResponse.getResults().isEmpty()) {
                        List<Future<MovieDetailsDTO>> futures = new ArrayList<>();
                        for (MovieDetailsDTO dto : movieResponse.getResults()) {
                            futures.add(executor.submit(() -> getMovieWithCredits(dto.getId())));
                        }
                        for (Future<MovieDetailsDTO> future : futures) {
                            try {
                                MovieDetailsDTO details = future.get();
                                if (details != null) {
                                    movies.add(details);
                                }
                            } catch (Exception e) {
                                System.err.println("Error fetching movie details: " + e.getMessage());
                            }
                        }
                        page++;
                        if (page > 100) morePages = false; // TMDB page limit
                    } else {
                        morePages = false;
                    }
                } else {
                    System.out.println("GET request failed. Status code: " + response.statusCode());
                    morePages = false;
                }

            } catch (Exception e) {
                System.err.println("Error fetching Danish movies: " + e.getMessage());
                e.printStackTrace();
                morePages = false;
            }
        }
        executor.shutdown();
        return movies;
    }


    public MovieDetailsDTO getMovieWithCredits(int id) {
        try {
            String url = MOVIE_BY_ID_URL + id + "?api_key=" + apiKey + "&language=da-DK" + "&append_to_response=credits";

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null) {
                return objectMapper.readValue(response.body(), MovieDetailsDTO.class);
            } else {
                System.out.println("GET request failed for movie id " + id + ". Status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error fetching movie with credits for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


            private static final String BASE_URL = "https://api.themoviedb.org/3/find/";

      public MovieResponseDTO getMovieInfoByImdbId(String imdbId) {
        objectMapper.registerModule(new JavaTimeModule());
        MovieResponseDTO movieResponse = null;

        try {
            String apiKey = System.getenv("API_KEY");
            if (apiKey == null) {
                throw new RuntimeException("API_KEY not found in environment variables!");
            }

            HttpClient client = HttpClient.newHttpClient();

            String url = BASE_URL + imdbId + "?api_key=" + apiKey + "&external_source=imdb_id";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                movieResponse = objectMapper.readValue(json, MovieResponseDTO.class);
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movieResponse;
    }



     public MovieResponseDTO getMovieInfoByTitle(String title) {
        objectMapper.registerModule(new JavaTimeModule());
        MovieResponseDTO movieResponse = null;

        try {
            String apiKey = System.getenv("API_KEY");
            if (apiKey == null) {
                throw new RuntimeException("API_KEY not found in environment variables!");
            }

            HttpClient client = HttpClient.newHttpClient();

            String safeTitle = title.replace(" ", "%20");

            String url = SEARCH_URL
                    + "?api_key=" + apiKey
                    + "&query=" + safeTitle;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                movieResponse = objectMapper.readValue(json, MovieResponseDTO.class);
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movieResponse;
    }
 */

}
