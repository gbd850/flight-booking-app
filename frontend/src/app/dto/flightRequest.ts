export interface FlightRequest {
    startDate?: Date;
    endDate?: Date;
    startLocation?: string;
    endLocation?: string;
    filterUnavailable?: boolean;
}