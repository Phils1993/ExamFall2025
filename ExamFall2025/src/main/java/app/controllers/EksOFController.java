package app.controllers;

public class EksOFController {
    /*
       private final DayService dayService;

    // Logger til generel logging
    private static final Logger logger = LoggerFactory.getLogger(DayController.class);

    // Logger til debug-specifik information (kan bruges til tracing)
    private static final Logger debugLogger = LoggerFactory.getLogger("app");

    public DayController(DayService dayService) {
        this.dayService = dayService;
    }

    @Override
    public Handler create() {
        return (Context ctx) -> {
            DayDTO dayDTO = ctx.bodyAsClass(DayDTO.class);
            if (dayDTO.getWeekId() == null) {
                ctx.status(HttpStatus.BAD_REQUEST).json("Week ID is required");
                return;
            }
            DayDTO newDayDTO = dayService.create(dayDTO, dayDTO.getWeekId());
            ctx.status(HttpStatus.CREATED).json(newDayDTO);
        };
    }

    @Override
    public Handler getAll() {
        return (Context ctx) -> {
            List<DayDTO> dayDTOs = dayService.getAll();
            ctx.status(HttpStatus.OK).json(dayDTOs);
        };
    }

    @Override
    public Handler update() {
        return (Context ctx) -> {
            int dayId = Integer.parseInt(ctx.pathParam("id"));
            DayDTO dayDTO = ctx.bodyAsClass(DayDTO.class);
            DayDTO newDayDTO = dayService.update(dayDTO, dayId);
            ctx.status(HttpStatus.OK).json(newDayDTO);
        };
    }

    @Override
    public Handler delete() {
        return (Context ctx) -> {
            int dayId = Integer.parseInt(ctx.pathParam("id"));
            dayService.delete(dayId);
            ctx.status(HttpStatus.NO_CONTENT).json("Day deleted successfully");
        };
    }

    @Override
    public Handler getById() {
        return (Context ctx) -> {
            int dayId = Integer.parseInt(ctx.pathParam("id"));
            DayDTO dayDTO = dayService.getById(dayId);
            ctx.status(HttpStatus.OK).json(dayDTO);
        };
    }
     */
}
