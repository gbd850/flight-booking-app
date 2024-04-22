import { Flight } from "./flight";

export interface Customer {
    id: number;
    username: string;
    password: string;
    role: string,
    bookedFlights: Flight[]
}