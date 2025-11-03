package app.routes;

public class EksForRoutes {

    /*

    // todo: brug dette som eks.

    private TripController tripController;

    public TripRoutes(TripService tripService) {
        this.tripController = new TripController(tripService);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            path("trips", () -> {
                get("", tripController.getAll(), Roles.ANYONE);
                post("", tripController.create(), Roles.ADMIN);
                get("{id}", tripController.getById(), Roles.ANYONE);
                put("{id}", tripController.update(), Roles.ADMIN);
                delete("{id}", tripController.delete(), Roles.ADMIN);

                // Fix: plural "guides" to match your test and spec
                put("{tripId}/guides/{guideId}", tripController.assignGuide(), Roles.ADMIN);

                get("guides/totalprice", tripController.getTotalTripValuePerGuide(), Roles.ADMIN);

                path("{id}/packing", () -> {
                    get("weight", tripController.getPackingWeight(), Roles.ANYONE);
                });
            });
        };
    }


    private GuideController guideController;

    public GuideRoutes(GuideService guideService) {
        this.guideController = new GuideController(guideService);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            path("guides", () -> {
                get("", guideController.getAll(), Roles.ANYONE);
                get("{id}", guideController.getById(), Roles.ANYONE);
                post("", guideController.create(), Roles.ADMIN);
                put("{id}", guideController.update(), Roles.ADMIN);
                delete("{id}", guideController.delete(), Roles.ADMIN);
            });
        };
    }

     */
}
