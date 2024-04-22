export interface Flight {
    id: number;
    name: string;
    price: number;
    startDate: Date;
    endDate?: Date;
    startLocation: string;
    endLocation?: string;
    isAvailable: boolean;
}